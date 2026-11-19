package com.netguard.app.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

/** Shared page frame: consistent gutters, title and scrolling for secondary screens. */
@Composable
fun ScreenScaffold(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.gutter, vertical = Spacing.lg),
    ) {
        Text(title, style = NetGuardTheme.type.title, color = NetGuardTheme.colors.textPrimary)
        Text(subtitle, style = NetGuardTheme.type.body, color = NetGuardTheme.colors.textSecondary)
        content()
    }
}
