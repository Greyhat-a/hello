package com.netguard.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.netguard.app.domain.model.NetworkEvent
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun NetworkEventRow(event: NetworkEvent, modifier: Modifier = Modifier) {
    val c = NetGuardTheme.colors
    val t = NetGuardTheme.type
    val tone = when (event.kind) {
        NetworkEvent.Kind.Connected -> PillTone.Success
        NetworkEvent.Kind.Disconnected -> PillTone.Error
        NetworkEvent.Kind.Changed -> PillTone.Neutral
    }

    Row(
        modifier.fillMaxWidth().padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusPill(event.kind.label, tone)
        Column {
            Text(event.message, style = t.body, color = c.textPrimary)
            Text(formatTimestamp(event.timestampMillis), style = t.label, color = c.textSecondary)
        }
    }
}

private fun formatTimestamp(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(timeFormatter)
