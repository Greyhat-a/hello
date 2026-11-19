package com.netguard.app.domain.model

/** A single connectivity change worth showing the user, newest events read first. */
data class NetworkEvent(
    val timestampMillis: Long,
    val kind: Kind,
    val message: String,
) {
    enum class Kind(val label: String) {
        Connected("Connected"),
        Disconnected("Disconnected"),
        Changed("Changed"),
    }
}
