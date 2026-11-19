package com.netguard.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.netguard.app.presentation.screens.analytics.AnalyticsScreen
import com.netguard.app.presentation.screens.diagnostics.DiagnosticsScreen
import com.netguard.app.presentation.screens.home.HomeScreen
import com.netguard.app.presentation.screens.optimize.OptimizeScreen
import com.netguard.app.presentation.screens.settings.SettingsScreen
import com.netguard.app.presentation.theme.NetGuardTheme

@Composable
fun NetGuardRoot() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val c = NetGuardTheme.colors

    Scaffold(
        containerColor = c.background,
        bottomBar = {
            NavigationBar(containerColor = c.surface, contentColor = c.textSecondary) {
                Destination.entries.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRoute == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label, style = NetGuardTheme.type.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = c.primary,
                            selectedTextColor = c.primary,
                            indicatorColor = c.surfaceHigh,
                            unselectedIconColor = c.textSecondary,
                            unselectedTextColor = c.textSecondary,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Home.route) { HomeScreen() }
            composable(Destination.Diagnostics.route) { DiagnosticsScreen() }
            composable(Destination.Optimize.route) { OptimizeScreen() }
            composable(Destination.Analytics.route) { AnalyticsScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
