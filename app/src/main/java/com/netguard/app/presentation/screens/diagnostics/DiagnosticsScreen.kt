package com.netguard.app.presentation.screens.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.netguard.app.presentation.components.EmptyState
import com.netguard.app.presentation.components.SectionHeader
import com.netguard.app.presentation.screens.ScreenScaffold
import com.netguard.app.presentation.theme.Spacing

private val plannedTests = listOf(
    "Internet connectivity", "DNS resolution", "DNS response time", "Latency",
    "Jitter", "Packet loss", "HTTPS connectivity", "Gateway reachability",
)

@Composable
fun DiagnosticsScreen() {
    ScreenScaffold(
        title = "Diagnostics",
        subtitle = "Each test reports status, result, interpretation, likely cause and a fix.",
    ) {
        SectionHeader(
            "Planned tests",
            subtitle = "Implemented in Phases 3 and 4. None have run.",
        )
        Column(Modifier.padding(bottom = Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            plannedTests.forEach { EmptyState(it, "Not run · implemented in a later phase") }
        }
    }
}
