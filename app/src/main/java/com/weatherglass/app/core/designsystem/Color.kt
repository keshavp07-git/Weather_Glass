package com.weatherglass.app.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object GlassColors {
    // Glass surface backgrounds with optimal opacity for high contrast readability
    val GlassSurfaceDark = Color(0x381E293B) // ~22% deep navy-slate
    val GlassSurfaceMedium = Color(0x4D1E293B) // ~30%
    val GlassSurfaceHeavy = Color(0x800F172A) // ~50%
    val GlassSurfaceHighlight = Color(0x2EFFFFFF) // Top specular highlight

    // Borders
    val GlassBorderLight = Color(0x38FFFFFF) // Delicate upper border
    val GlassBorderSubtle = Color(0x1FFFFFFF)

    // Text & Foreground
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xD8E2E8F0)
    val TextTertiary = Color(0x9994A3B8)
    val TextMuted = Color(0x6694A3B8)

    // Accents
    val AccentGold = Color(0xFFFFD166)
    val AccentCyan = Color(0xFF38BDF8)
    val AccentTeal = Color(0xFF2DD4BF)
    val AccentRose = Color(0xFFFB7185)
    val AccentIndigo = Color(0xFF818CF8)

    // Gradients for weather atmospheres
    val ClearDayGradient = listOf(
        Color(0xFF0284C7), // Sky blue top
        Color(0xFF0369A1),
        Color(0xFF0C4A6E)  // Deep marine bottom
    )

    val ClearNightGradient = listOf(
        Color(0xFF020617), // Deep midnight top
        Color(0xFF0F172A),
        Color(0xFF1E1B4B)  // Dark violet-navy bottom
    )

    val PartlyCloudyDayGradient = listOf(
        Color(0xFF0284C7),
        Color(0xFF334155),
        Color(0xFF1E293B)
    )

    val PartlyCloudyNightGradient = listOf(
        Color(0xFF0B132B),
        Color(0xFF1C2541),
        Color(0xFF0F172A)
    )

    val OvercastGradient = listOf(
        Color(0xFF334155),
        Color(0xFF475569),
        Color(0xFF1E293B)
    )

    val RainGradient = listOf(
        Color(0xFF1E293B),
        Color(0xFF0F172A),
        Color(0xFF082F49)
    )

    val ThunderstormGradient = listOf(
        Color(0xFF0F172A),
        Color(0xFF1E1B4B),
        Color(0xFF020617)
    )

    val SnowGradient = listOf(
        Color(0xFF334155),
        Color(0xFF475569),
        Color(0xFF64748B)
    )

    val SunsetGradient = listOf(
        Color(0xFF431407), // Deep ember top
        Color(0xFF9A3412), // Orange
        Color(0xFF701A75), // Purple
        Color(0xFF1E1B4B)  // Navy base
    )

    val FogGradient = listOf(
        Color(0xFF334155),
        Color(0xFF475569),
        Color(0xFF334155)
    )
}
