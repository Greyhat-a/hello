package com.netguard.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netguard.app.presentation.theme.NetGuardShapes
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

/** Honest placeholder for functionality that is not built yet or has no data. */
@Composable
fun EmptyState(title: String, message: String, modifier: Modifier = Modifier) {
    val c = NetGuardTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = NetGuardShapes.tile,
        color = c.surface,
        border = BorderStroke(1.dp, c.outline),
    ) {
        Column(Modifier.padding(Spacing.lg)) {
            Text(title, style = NetGuardTheme.type.subtitle, color = c.textPrimary)
            Text(message, style = NetGuardTheme.type.body, color = c.textSecondary)
        }
    }
}
