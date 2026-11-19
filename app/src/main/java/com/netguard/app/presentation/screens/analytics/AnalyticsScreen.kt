package com.netguard.app.presentation.screens.analytics

import androidx.compose.runtime.Composable
import com.netguard.app.presentation.components.EmptyState
import com.netguard.app.presentation.components.SectionHeader
import com.netguard.app.presentation.screens.ScreenScaffold

@Composable
fun AnalyticsScreen() {
    ScreenScaffold(
        title = "Analytics",
        subtitle = "Trends over time from stored measurements.",
    ) {
        SectionHeader("History")
        EmptyState(
            "No history yet",
            "Charts for speed, latency, stability and interruptions appear once " +
                "measurements are stored (Phase 5).",
        )
    }
}
