package com.netguard.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MeasurementTest {

    @Test
    fun measured_returnsValue() {
        assertEquals(42.0, Measurement.Measured(42.0, 1L).valueOrNull()!!, 0.0)
    }

    @Test
    fun estimated_returnsValue() {
        assertEquals(7, Measurement.Estimated(7, "RSSI-derived", 1L).valueOrNull())
    }

    @Test
    fun unavailable_failed_notPermitted_neverYieldAValue() {
        val states: List<Measurement<Double>> = listOf(
            Measurement.Unavailable("Not measured yet"),
            Measurement.Failed("Timeout"),
            Measurement.NotPermitted("Location permission required"),
        )
        states.forEach { assertNull(it.valueOrNull()) }
    }
}
