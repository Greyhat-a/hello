package com.netguard.app.domain.model

import com.netguard.app.core.Measurement

/**
 * A snapshot of the device's active network, as NETGUARD understands it right now.
 *
 * [networkName] and [signalStrength] are [Measurement]s rather than bare values because
 * either can be genuinely unavailable (no Wi-Fi connection), permission-gated (SSID needs
 * Location on Android), or an estimate derived from another reading (signal percentage from
 * RSSI) — the three states this app must never collapse into one "—".
 */
data class ConnectionState(
    val isConnected: Boolean,
    val isValidated: Boolean,
    val isMetered: Boolean,
    val type: ConnectionType,
    val networkName: Measurement<String>,
    val signalStrength: Measurement<Int>,
) {
    companion object {
        val Initial = ConnectionState(
            isConnected = false,
            isValidated = false,
            isMetered = false,
            type = ConnectionType.NONE,
            networkName = Measurement.Unavailable("Not measured yet"),
            signalStrength = Measurement.Unavailable("Not measured yet"),
        )
    }
}
