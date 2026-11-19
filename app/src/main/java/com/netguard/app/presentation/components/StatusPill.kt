package com.netguard.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import com.netguard.app.presentation.theme.NetGuardShapes
import com.netguard.app.presentation.theme.NetGuardTheme
import com.netguard.app.presentation.theme.Spacing

enum class PillTone { Neutral, Success, Warning, Error }

@Composable
fun StatusPill(text: String, tone: PillTone, modifier: Modifier = Modifier) {
    val c = NetGuardTheme.colors
    val accent: Color = when (tone) {
        PillTone.Neutral -> c.textSecondary
        PillTone.Success -> c.success
        PillTone.Warning -> c.warning
        PillTone.Error -> c.error
    }
    Text(
        text = text,
        style = NetGuardTheme.type.label,
        color = accent,
        modifier = modifier
            .clip(NetGuardShapes.pill)
            .background(accent.copy(alpha = 0.14f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}
