package com.netguard.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 4-dp base grid. */
object Spacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp

    /** Horizontal screen gutter. */
    val gutter: Dp = 16.dp
}

object NetGuardShapes {
    val tile = RoundedCornerShape(8.dp)
    val pill = RoundedCornerShape(999.dp)
}
