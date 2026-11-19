package com.netguard.app.presentation.screens.settings

import androidx.compose.runtime.Composable
import com.netguard.app.BuildConfig
import com.netguard.app.presentation.components.EmptyState
import com.netguard.app.presentation.components.SectionHeader
import com.netguard.app.presentation.screens.ScreenScaffold

@Composable
fun SettingsScreen() {
    ScreenScaffold(
        title = "Settings",
        subtitle = "Theme, monitoring, notifications and data preferences.",
    ) {
        SectionHeader("Preferences")
        EmptyState(
            "Coming with DataStore",
            "Theme, monitoring frequency, notifications and data-collection options " +
                "are added once persistence exists.",
        )
        SectionHeader("About")
        EmptyState("NETGUARD", "Version ${BuildConfig.VERSION_NAME}")
    }
}
