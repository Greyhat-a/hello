package com.netguard.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector

/** The five top-level destinations, in bottom-bar order. */
enum class Destination(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Outlined.Home),
    Diagnostics("diagnostics", "Diagnostics", Icons.Outlined.HealthAndSafety),
    Optimize("optimize", "Optimize", Icons.Outlined.Tune),
    Analytics("analytics", "Analytics", Icons.Outlined.BarChart),
    Settings("settings", "Settings", Icons.Outlined.Settings),
}
