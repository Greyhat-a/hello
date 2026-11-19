package com.netguard.app.core

/**
 * Every metric NetGuard shows is wrapped in a [Measurement]. This makes it
 * impossible to display a number without knowing where it came from, and keeps
 * "the test failed" distinct from "Android does not allow this test".
 */
sealed interface Measurement<out T> {

    /** A value actually observed by an active or passive measurement. */
    data class Measured<T>(val value: T, val timestampMillis: Long) : Measurement<T>

    /** A derived or system-reported approximation. [basis] explains how. */
    data class Estimated<T>(val value: T, val basis: String, val timestampMillis: Long) : Measurement<T>

    /** Nothing has been measured yet, or the device cannot supply the metric. */
    data class Unavailable(val reason: String) : Measurement<Nothing>

    /** The measurement was attempted and failed (timeout, DNS error, no route...). */
    data class Failed(val error: String) : Measurement<Nothing>

    /** Android or a missing permission prevents this measurement. */
    data class NotPermitted(val restriction: String) : Measurement<Nothing>
}

/** Returns the numeric/typed value only when it was genuinely measured or estimated. */
fun <T> Measurement<T>.valueOrNull(): T? = when (this) {
    is Measurement.Measured -> value
    is Measurement.Estimated -> value
    else -> null
}
