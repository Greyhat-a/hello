package com.netguard.app.presentation.screens.optimize

import androidx.compose.runtime.Composable
import com.netguard.app.presentation.components.EmptyState
import com.netguard.app.presentation.components.SectionHeader
import com.netguard.app.presentation.screens.ScreenScaffold

@Composable
fun OptimizeScreen() {
    ScreenScaffold(
        title = "Optimize",
        subtitle = "Recommendations based on measured results, using only what Android permits.",
    ) {
        SectionHeader("Recommendations")
        EmptyState(
            "Nothing to recommend yet",
            "Recommendations require diagnostic results. They arrive with Phase 4.",
        )
        SectionHeader("What NetGuard cannot do")
        EmptyState(
            "Android limits",
            "NetGuard cannot raise your plan's bandwidth, change cellular radio settings, " +
                "or prioritise traffic over other apps without system privileges.",
        )
    }
}
