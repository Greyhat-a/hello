package com.netguard.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Semantic colour tokens. Blue is interactive/primary data; green/amber/red are status only. */
@Immutable
data class NetGuardColors(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
)

val DarkColors = NetGuardColors(
    background = Color(0xFF0A0F1C),
    surface = Color(0xFF111827),
    surfaceHigh = Color(0xFF1A2233),
    outline = Color(0xFF263146),
    primary = Color(0xFF2F7BFF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF4FB8C9),
    textPrimary = Color(0xFFE8EDF5),
    textSecondary = Color(0xFF8B97AB),
    success = Color(0xFF3DB37A),
    warning = Color(0xFFE0A030),
    error = Color(0xFFD9534F),
)

val LightColors = NetGuardColors(
    background = Color(0xFFF4F6FA),
    surface = Color(0xFFFFFFFF),
    surfaceHigh = Color(0xFFE9EDF4),
    outline = Color(0xFFD5DBE6),
    primary = Color(0xFF1B5FE0),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2A8E9F),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF556277),
    success = Color(0xFF2A8F5E),
    warning = Color(0xFFB8801A),
    error = Color(0xFFB8403C),
)
