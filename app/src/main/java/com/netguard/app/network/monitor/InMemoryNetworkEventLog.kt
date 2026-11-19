package com.netguard.app.network.monitor

import com.netguard.app.domain.model.NetworkEvent
import com.netguard.app.domain.monitor.NetworkEventLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Keeps the most recent [MAX_EVENTS] connectivity events in memory, newest first. */
@Singleton
class InMemoryNetworkEventLog @Inject constructor() : NetworkEventLog {

    private val _events = MutableStateFlow<List<NetworkEvent>>(emptyList())
    override val events: StateFlow<List<NetworkEvent>> = _events.asStateFlow()

    override fun record(event: NetworkEvent) {
        _events.update { current -> (listOf(event) + current).take(MAX_EVENTS) }
    }

    private companion object {
        const val MAX_EVENTS = 50
    }
}
