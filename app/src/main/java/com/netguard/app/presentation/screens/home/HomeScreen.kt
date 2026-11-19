package com.netguard.app.presentation.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.netguard.app.core.Measurement
import com.netguard.app.domain.model.NetworkEvent
import com.netguard.app.presentation.components.ConnectionCard
import com.netguard.app.presentation.components.EmptyState
import com.netguard.app.presentation.components.MetricTile
import com.netguard.app.presentation.components.NetworkEventRow
import com.netguard.app.presentation.components.SectionHeader
import com.netguard.app.presentation.theme.NetGuardShapes
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

private const val NOT_MEASURED = "Not measured yet"

/**
 * Connectivity (Phase 2) is live: [ConnectionCard] and recent events reflect the real device
 * state. Performance metrics below stay explicitly [Measurement.Unavailable] until Phase 3
 * adds the speed, latency, jitter and loss engines.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val c = NetGuardTheme.colors
    val t = NetGuardTheme.type

    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { viewModel.refreshConnectionInfo() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.gutter, vertical = Spacing.lg),
    ) {
        Text("NETGUARD", style = t.label, color = c.primary)
        Text("Network health", style = t.title, color = c.textPrimary)

        ConnectionCard(
            state = connectionState,
            onRequestLocationPermission = {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier.padding(top = Spacing.sm),
        )

        SectionHeader("Performance")
        MetricRow(
            MetricSpec("Download", "Mbps"), MetricSpec("Upload", "Mbps"),
        )
        Spacer8()
        MetricRow(
            MetricSpec("Latency", "ms"), MetricSpec("Jitter", "ms"),
        )
        Spacer8()
        MetricRow(MetricSpec("Packet loss", "%"), null)

        SectionHeader(
            "Health score",
            subtitle = "Calculated only from metrics that were actually measured.",
        )
        EmptyState(
            "No score yet",
            "Run a speed test to measure download, upload, latency, jitter and loss. " +
                "The score and its breakdown appear here.",
        )

        Button(
            onClick = { /* Speed test is implemented in Phase 3. */ },
            enabled = false,
            shape = NetGuardShapes.tile,
            colors = ButtonDefaults.buttonColors(
                containerColor = c.primary,
                disabledContainerColor = c.surfaceHigh,
                disabledContentColor = c.textSecondary,
            ),
            modifier = Modifier
                .padding(top = Spacing.xl)
                .fillMaxWidth()
                .height(52.dp),
        ) { Text("Run speed test · available in Phase 3", style = t.subtitle) }

        SectionHeader("Live performance")
        EmptyState("No data yet", "The real-time graph appears once monitoring is running.")

        SectionHeader("Recent events")
        if (events.isEmpty()) {
            EmptyState("No events recorded", "Connection changes and interruptions will be listed here.")
        } else {
            Column {
                events.forEach { event: NetworkEvent -> NetworkEventRow(event) }
            }
        }
    }
}

private data class MetricSpec(val label: String, val unit: String)

@Composable
private fun MetricRow(first: MetricSpec, second: MetricSpec?) {
    Row(
        Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        MetricTile(
            label = first.label,
            measurement = Measurement.Unavailable(NOT_MEASURED),
            unit = first.unit,
            modifier = Modifier.weight(1f).fillMaxSize(),
        )
        if (second != null) {
            MetricTile(
                label = second.label,
                measurement = Measurement.Unavailable(NOT_MEASURED),
                unit = second.unit,
                modifier = Modifier.weight(1f).fillMaxSize(),
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun Spacer8() = Spacer(Modifier.height(Spacing.sm))
