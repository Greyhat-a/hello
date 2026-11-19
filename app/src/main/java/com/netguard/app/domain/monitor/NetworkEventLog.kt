package com.netguard.app.domain.monitor

import com.netguard.app.domain.model.NetworkEvent
import kotlinx.coroutines.flow.StateFlow

/**
 * A rolling log of connectivity changes, newest first. In-memory only for now; Phase 5 adds a
 * Room-backed implementation so events survive process death, behind the same interface.
 */
interface NetworkEventLog {
    val events: StateFlow<List<NetworkEvent>>
    fun record(event: NetworkEvent)
}
