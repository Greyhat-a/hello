package com.netguard.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalNetGuardColors = staticCompositionLocalOf { DarkColors }
private val LocalNetGuardTypography = staticCompositionLocalOf { DefaultTypography }

/** Access design tokens via `NetGuardTheme.colors` / `NetGuardTheme.type`. */
object NetGuardTheme {
    val colors: NetGuardColors
        @Composable @ReadOnlyComposable get() = LocalNetGuardColors.current
    val type: NetGuardTypography
        @Composable @ReadOnlyComposable get() = LocalNetGuardTypography.current
}

@Composable
fun NetGuardTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    // Material3 is mapped to our tokens so stock components (NavigationBar, Switch...)
    // inherit the NetGuard palette instead of default Material colours.
    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary, onPrimary = colors.onPrimary,
            secondary = colors.secondary,
            background = colors.background, onBackground = colors.textPrimary,
            surface = colors.surface, onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceHigh, onSurfaceVariant = colors.textSecondary,
            outline = colors.outline, error = colors.error,
        )
    } else {
        lightColorScheme(
            primary = colors.primary, onPrimary = colors.onPrimary,
            secondary = colors.secondary,
            background = colors.background, onBackground = colors.textPrimary,
            surface = colors.surface, onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceHigh, onSurfaceVariant = colors.textSecondary,
            outline = colors.outline, error = colors.error,
        )
    }

    CompositionLocalProvider(
        LocalNetGuardColors provides colors,
        LocalNetGuardTypography provides DefaultTypography,
    ) {
        MaterialTheme(colorScheme = materialScheme, content = content)
    }
}
