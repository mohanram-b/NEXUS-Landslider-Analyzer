package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexusColorScheme = darkColorScheme(
    primary = NexusAccent,
    onPrimary = Color.White,
    primaryContainer = NexusSurfaceSecondary,
    onPrimaryContainer = NexusTextPrimary,
    secondary = NexusAccentLight,
    onSecondary = Color.Black,
    secondaryContainer = NexusSurfaceTertiary,
    onSecondaryContainer = NexusTextPrimary,
    tertiary = StatusAnalyzing,
    onTertiary = Color.White,
    background = NexusBackground,
    onBackground = NexusTextPrimary,
    surface = NexusSurface,
    onSurface = NexusTextPrimary,
    surfaceVariant = NexusSurfaceSecondary,
    onSurfaceVariant = NexusTextSecondary,
    outline = NexusBorder,
    outlineVariant = NexusBorderFocus,
    error = StatusCritical,
    onError = Color.White,
    errorContainer = StatusCriticalBg,
    onErrorContainer = StatusCritical
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // NEXUS enforces its cohesive minimal high-contrast dark theme
    MaterialTheme(
        colorScheme = NexusColorScheme,
        typography = Typography,
        content = content
    )
}
