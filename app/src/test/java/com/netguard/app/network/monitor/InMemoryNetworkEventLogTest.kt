package com.netguard.app.network.monitor

import com.netguard.app.domain.model.NetworkEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryNetworkEventLogTest {

    @Test
    fun record_prependsNewestFirst() {
        val log = InMemoryNetworkEventLog()
        log.record(NetworkEvent(1L, NetworkEvent.Kind.Connected, "a"))
        log.record(NetworkEvent(2L, NetworkEvent.Kind.Disconnected, "b"))

        assertEquals(listOf("b", "a"), log.events.value.map { it.message })
    }

    @Test
    fun record_capsAtFiftyEntries() {
        val log = InMemoryNetworkEventLog()
        repeat(60) { i -> log.record(NetworkEvent(i.toLong(), NetworkEvent.Kind.Changed, "event-$i")) }

        assertEquals(50, log.events.value.size)
        assertEquals("event-59", log.events.value.first().message)
    }
}
