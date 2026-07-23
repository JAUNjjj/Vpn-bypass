package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CosmicColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = ElectricBlue,
    tertiary = NeonEmerald,
    background = CosmicBackground,
    surface = CosmicSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = WarningRed
)

private val LightCosmicColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = NeonCyan,
    tertiary = NeonEmerald,
    background = TextPrimary,
    surface = Color(0xFFE3E9F8),
    onBackground = CosmicBackground,
    onSurface = CosmicBackground,
    surfaceVariant = Color(0xFFCAD4EA),
    onSurfaceVariant = TextMuted,
    outline = TextSecondary,
    error = WarningRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark theme for the core VPN futuristic sci-fi Earth look
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CosmicColorScheme else LightCosmicColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
