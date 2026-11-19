package com.netguard.app.presentation.components

import com.netguard.app.core.Measurement

/**
 * Shared rendering rules for [Measurement]: a real value shows plainly, an estimate is marked
 * as one, and anything else shows its reason instead of a dash with no explanation.
 */
fun Measurement<String>.displayValue(): String = when (this) {
    is Measurement.Measured -> value
    is Measurement.Estimated -> value
    is Measurement.Unavailable -> "— $reason"
    is Measurement.Failed -> "— $error"
    is Measurement.NotPermitted -> "— $restriction"
}

fun Measurement<Int>.displayPercent(): String = when (this) {
    is Measurement.Measured -> "$value%"
    is Measurement.Estimated -> "~$value%"
    is Measurement.Unavailable -> "— $reason"
    is Measurement.Failed -> "— $error"
    is Measurement.NotPermitted -> "— $restriction"
}

/** True only for a state the "Allow" affordance should be shown for. */
fun <T> Measurement<T>.isNotPermitted(): Boolean = this is Measurement.NotPermitted
