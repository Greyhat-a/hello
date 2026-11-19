package com.netguard.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.padding(top = Spacing.xl, bottom = Spacing.sm)) {
        Text(title, style = NetGuardTheme.type.subtitle, color = NetGuardTheme.colors.textPrimary)
        if (subtitle != null) {
            Text(subtitle, style = NetGuardTheme.type.body, color = NetGuardTheme.colors.textSecondary)
        }
    }
}
