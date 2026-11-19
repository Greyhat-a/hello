package com.netguard.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netguard.app.core.Measurement
import com.netguard.app.presentation.theme.NetGuardShapes
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

/**
 * A single metric. Renders a value only for [Measurement.Measured]/[Measurement.Estimated];
 * every other state shows an explicit dash plus the reason, never a placeholder number.
 */
@Composable
fun MetricTile(
    label: String,
    measurement: Measurement<String>,
    modifier: Modifier = Modifier,
    unit: String? = null,
) {
    val c = NetGuardTheme.colors
    val t = NetGuardTheme.type

    val (valueText, note, noteColor) = when (measurement) {
        is Measurement.Measured -> Triple(measurement.value, null, c.textSecondary)
        is Measurement.Estimated -> Triple(measurement.value, "Estimate · ${measurement.basis}", c.textSecondary)
        is Measurement.Unavailable -> Triple("—", measurement.reason, c.textSecondary)
        is Measurement.Failed -> Triple("—", "Failed · ${measurement.error}", c.error)
        is Measurement.NotPermitted -> Triple("—", "Restricted · ${measurement.restriction}", c.warning)
    }

    Surface(
        modifier = modifier,
        shape = NetGuardShapes.tile,
        color = c.surface,
        border = BorderStroke(1.dp, c.outline),
    ) {
        Column(Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(label.uppercase(), style = t.label, color = c.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(valueText, style = t.display.copy(fontSize = t.title.fontSize), color = c.textPrimary)
                if (unit != null && measurement is Measurement.Measured) {
                    Text(unit, style = t.label, color = c.textSecondary)
                }
            }
            if (note != null) Text(note, style = t.label, color = noteColor)
        }
    }
}
