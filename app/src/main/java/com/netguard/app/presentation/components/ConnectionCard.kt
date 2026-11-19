package com.netguard.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netguard.app.domain.model.ConnectionState
import com.netguard.app.presentation.theme.NetGuardShapes
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

/** The Home dashboard's "Connection status" block: state, type, Wi-Fi name and signal. */
@Composable
fun ConnectionCard(
    state: ConnectionState,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NetGuardTheme.colors
    val t = NetGuardTheme.type

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = NetGuardShapes.tile,
        color = c.surface,
        border = BorderStroke(1.dp, c.outline),
    ) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                StatusPill(statusText(state), statusTone(state))
                if (state.isConnected && state.isMetered) {
                    StatusPill("Metered", PillTone.Warning)
                }
            }
            InfoRow("Connection type", state.type.displayName)
            InfoRow("Network name", state.networkName.displayValue())
            InfoRow("Signal strength", state.signalStrength.displayPercent())

            if (state.networkName.isNotPermitted()) {
                Column {
                    Text(
                        "Android requires Location permission to read the Wi-Fi name.",
                        style = t.label,
                        color = c.textSecondary,
                    )
                    TextButton(onClick = onRequestLocationPermission) {
                        Text("Allow", style = t.body, color = c.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val c = NetGuardTheme.colors
    val t = NetGuardTheme.type
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = t.body, color = c.textSecondary)
        Text(value, style = t.subtitle, color = c.textPrimary)
    }
}

private fun statusText(state: ConnectionState): String = when {
    !state.isConnected -> "Not connected"
    !state.isValidated -> "Connected · No internet"
    else -> "Connected"
}

private fun statusTone(state: ConnectionState): PillTone = when {
    !state.isConnected -> PillTone.Error
    !state.isValidated -> PillTone.Warning
    else -> PillTone.Success
}
