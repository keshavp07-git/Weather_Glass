package com.weatherglass.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.weatherglass.app.domain.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = GlassColors.AccentCyan,
    onPrimary = Color.Black,
    secondary = GlassColors.AccentTeal,
    onSecondary = Color.Black,
    surface = Color(0x2B1E293B),
    onSurface = GlassColors.TextPrimary,
    background = Color(0xFF0A0F1D),
    onBackground = GlassColors.TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = GlassColors.AccentCyan,
    onPrimary = Color.White,
    secondary = GlassColors.AccentTeal,
    onSecondary = Color.White,
    surface = Color(0x40FFFFFF),
    onSurface = Color(0xFF0F172A),
    background = Color(0xFFE2E8F0),
    onBackground = Color(0xFF0F172A)
)

@Composable
fun WeatherGlassTheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WeatherGlassTypography,
        content = content
    )
}
