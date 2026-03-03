package com.example.Zenith.presenters.home.view.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DayColors {
    val gradientStart = Color(0xFF1976D2)
    val gradientMid = Color(0xFF2196F3)
    val gradientEnd = Color(0xFF64B5F6)

    val accent = Color(0xFFFFCA28)
    val glassWhite = Color(0x77FFFFFF)
    val sunGlow = Color(0xFFFFE082)

    val onGlass = Color(0xFFFFFFFF)
    val onGlassMuted = Color(0xBBFFFFFF)
}

object NightColors {
    val gradientStart  = Color(0xFF0D1B2A)
    val gradientMid    = Color(0xFF1B2838)
    val gradientEnd    = Color(0xFF2C3E50)
    val glassWhite     = Color(0x22FFFFFF)
    val glassBorder    = Color(0x33AAAAFF)
    val onGlass        = Color.White
    val onGlassMuted   = Color(0xBBCCCCFF)
    val moonGlow       = Color(0xFFE8EAF6)
    val accent         = Color(0xFFB0BEC5)
    val starColor      = Color(0xFFECEFF1)
}

fun Modifier.glassCard(isDay: Boolean, cornerRadius: Dp = 20.dp): Modifier = this
    .background(
        color = if (isDay) DayColors.glassWhite else NightColors.glassWhite,
        shape = RoundedCornerShape(cornerRadius)
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                if (isDay) Color(0x88FFFFFF) else Color(0x44AAAAFF),
                if (isDay) Color(0x22FFFFFF) else Color(0x11FFFFFF)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )