package com.brewkery.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BrewColors {
    val Cream = Color(0xFFFFFAF6)
    val Espresso = Color(0xFF140A07)
    val Accent = Color(0xFFD9532B)
    val AccentSoft = Color(0xFFF8EAE2)
    val Border = Color(0xFFEBDDD3)
    val Muted = Color(0xFF7A6A62)
    val BadgeBg = Color(0xFFFCEFC3)
    val BadgeText = Color(0xFF6B4300)
    val Success = Color(0xFF0E8A5F)
    val Amber = Color(0xFFF2A310)
    val Danger = Color(0xFFE0264F)
}

private val LightColors = lightColorScheme(
    primary = BrewColors.Accent,
    onPrimary = Color.White,
    background = BrewColors.Cream,
    onBackground = BrewColors.Espresso,
    surface = Color.White,
    onSurface = BrewColors.Espresso,
    error = BrewColors.Danger,
)

@Composable
fun BrewkeryTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
