package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VellorPitchBlack,
    onPrimary = VellorWhite,
    primaryContainer = VellorLightCard,
    onPrimaryContainer = VellorBlack,
    secondary = VellorMuted,
    onSecondary = VellorWhite,
    background = VellorWhite,
    onBackground = VellorBlack,
    surface = VellorLightCard,
    onSurface = VellorBlack,
    surfaceVariant = VellorLightSurface,
    onSurfaceVariant = VellorMuted,
    outline = VellorLightBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = VellorWhite,
    onPrimary = VellorBlack,
    primaryContainer = Color(0xFF18181B),
    onPrimaryContainer = VellorWhite,
    secondary = Color(0xFFA1A1AA),
    onSecondary = VellorBlack,
    background = Color(0xFF09090B),
    onBackground = VellorWhite,
    surface = Color(0xFF141416),
    onSurface = VellorWhite,
    surfaceVariant = Color(0xFF27272A),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF27272A)
)

@Composable
fun VellorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
