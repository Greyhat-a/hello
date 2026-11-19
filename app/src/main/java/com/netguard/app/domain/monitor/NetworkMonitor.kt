package com.netguard.app.domain.monitor

import com.netguard.app.domain.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Observes connectivity changes and the capabilities of the device's current default network.
 * Implemented by [com.netguard.app.network.monitor.ConnectivityNetworkMonitor] on top of
 * [android.net.ConnectivityManager]; kept as an interface here so the domain and ViewModel
 * layers depend on behaviour, not on Android framework classes.
 */
interface NetworkMonitor {
    val connectionState: StateFlow<ConnectionState>

    /**
     * Re-reads the current state without waiting for a connectivity callback. Needed after a
     * runtime permission that affects a reading — e.g. Location, which gates the Wi-Fi name —
     * changes, since granting it does not by itself fire a connectivity event.
     */
    fun refresh()
}
