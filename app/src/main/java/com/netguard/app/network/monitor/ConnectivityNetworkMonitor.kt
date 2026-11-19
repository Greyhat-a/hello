package com.netguard.app.network.monitor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.netguard.app.core.Measurement
import com.netguard.app.domain.model.ConnectionState
import com.netguard.app.domain.model.ConnectionType
import com.netguard.app.domain.model.NetworkEvent
import com.netguard.app.domain.monitor.NetworkEventLog
import com.netguard.app.domain.monitor.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks the device's current default network with [ConnectivityManager.registerDefaultNetworkCallback]
 * — the same signal Android itself uses to decide which network carries app traffic, so
 * "Connected" here means what the user expects it to mean.
 *
 * Signal strength for Wi-Fi is read from [WifiInfo.getRssi], which needs only
 * [Manifest.permission.ACCESS_WIFI_STATE] (a normal, install-time permission). The Wi-Fi name
 * (SSID) is a separate Android restriction: it requires Location permission *and* Location
 * services to be on, because the OS treats SSID lookup as a location signal. Cellular signal
 * detail is deferred to the dedicated Signal Analyzer phase rather than shipped against an
 * unverified permission assumption.
 */
@Singleton
class ConnectivityNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventLog: NetworkEventLog,
) : NetworkMonitor {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    private val _connectionState = MutableStateFlow(ConnectionState.Initial)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    /** The first [update] call reflects startup state, not a real transition — don't log it. */
    private val hasEmittedInitialState = AtomicBoolean(false)

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = update()
        override fun onLost(network: Network) = update()
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) = update()
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callback, Handler(Looper.getMainLooper()))
        update()
    }

    override fun refresh() = update()

    private fun update() {
        val next = buildConnectionState()
        val previous = _connectionState.getAndUpdate { next }
        if (hasEmittedInitialState.getAndSet(true)) {
            recordTransition(previous, next)
        }
    }

    private fun recordTransition(previous: ConnectionState, next: ConnectionState) {
        val now = System.currentTimeMillis()
        val event = when {
            !previous.isConnected && next.isConnected ->
                NetworkEvent(now, NetworkEvent.Kind.Connected, "Connected via ${next.type.displayName}")
            previous.isConnected && !next.isConnected ->
                NetworkEvent(now, NetworkEvent.Kind.Disconnected, "Disconnected")
            previous.isConnected && next.isConnected && previous.type != next.type ->
                NetworkEvent(
                    now,
                    NetworkEvent.Kind.Changed,
                    "Switched from ${previous.type.displayName} to ${next.type.displayName}",
                )
            else -> null
        }
        event?.let(eventLog::record)
    }

    private fun buildConnectionState(): ConnectionState {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let(connectivityManager::getNetworkCapabilities)
            ?: return ConnectionState.Initial.copy(
                networkName = Measurement.Unavailable("No active network"),
                signalStrength = Measurement.Unavailable("No active network"),
            )

        val type = connectionTypeOf(capabilities)
        return ConnectionState(
            isConnected = true,
            isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
            isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
            type = type,
            networkName = networkNameOf(type, capabilities),
            signalStrength = signalStrengthOf(type, capabilities),
        )
    }

    private fun connectionTypeOf(capabilities: NetworkCapabilities): ConnectionType = when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
        else -> ConnectionType.OTHER
    }

    private fun networkNameOf(type: ConnectionType, capabilities: NetworkCapabilities): Measurement<String> {
        if (type != ConnectionType.WIFI) {
            return Measurement.Unavailable("Only shown for Wi-Fi")
        }
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasLocationPermission) {
            return Measurement.NotPermitted("Needs Location permission — Android's own requirement for reading the Wi-Fi name")
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null || !LocationManagerCompat.isLocationEnabled(locationManager)) {
            return Measurement.Unavailable("Turn on device Location to show the Wi-Fi name")
        }
        val ssid = wifiInfoOf(capabilities)?.ssid?.trim('"')
        return if (ssid.isNullOrBlank() || ssid == UNKNOWN_SSID) {
            Measurement.Unavailable("Not available")
        } else {
            Measurement.Measured(ssid, System.currentTimeMillis())
        }
    }

    private fun signalStrengthOf(type: ConnectionType, capabilities: NetworkCapabilities): Measurement<Int> {
        if (type != ConnectionType.WIFI) {
            return Measurement.Unavailable("Cellular signal arrives with the Signal Analyzer in a later phase")
        }
        val rssi = wifiInfoOf(capabilities)?.rssi ?: return Measurement.Unavailable("Not available")
        val level = WifiManager.calculateSignalLevel(rssi, WIFI_SIGNAL_LEVELS)
        val percent = (level * 100) / (WIFI_SIGNAL_LEVELS - 1)
        return Measurement.Estimated(
            percent,
            "From RSSI ($rssi dBm) via Android's signal-level scale",
            System.currentTimeMillis(),
        )
    }

    /** [NetworkCapabilities.getTransportInfo] needs API 29+; older levels fall back to [WifiManager]. */
    private fun wifiInfoOf(capabilities: NetworkCapabilities): WifiInfo? {
        val fromCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            capabilities.transportInfo as? WifiInfo
        } else {
            null
        }
        if (fromCapabilities != null) return fromCapabilities
        @Suppress("DEPRECATION")
        return wifiManager?.connectionInfo
    }

    private companion object {
        const val UNKNOWN_SSID = "<unknown ssid>"
        const val WIFI_SIGNAL_LEVELS = 5
    }
}
