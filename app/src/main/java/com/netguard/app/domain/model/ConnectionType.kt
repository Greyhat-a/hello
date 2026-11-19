package com.netguard.app.domain.model

/** The transport actually carrying traffic right now, as reported by [android.net.NetworkCapabilities]. */
enum class ConnectionType(val displayName: String) {
    WIFI("Wi-Fi"),
    CELLULAR("Cellular"),
    ETHERNET("Ethernet"),
    VPN("VPN"),
    OTHER("Other"),
    NONE("None"),
}
