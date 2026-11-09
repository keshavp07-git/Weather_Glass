package com.weatherglass.app.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weatherglass.app.domain.model.WeatherCondition

@Composable
fun HeroWeatherIllustration(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w * 0.5f, h * 0.5f)

        when (condition) {
            WeatherCondition.CLEAR_DAY -> {
                // Large radiant aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x66FFD166),
                            Color(0x33FFB300),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.5f
                    ),
                    radius = w * 0.5f,
                    center = center
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300)),
                        center = center,
                        radius = w * 0.22f
                    ),
                    radius = w * 0.22f,
                    center = center
                )
            }
            WeatherCondition.CLEAR_NIGHT -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x44818CF8),
                            Color(0x2238BDF8),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.5f
                    ),
                    radius = w * 0.5f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFFF8FAFC),
                    radius = w * 0.24f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = w * 0.22f,
                    center = Offset(center.x + w * 0.1f, center.y - w * 0.06f)
                )
            }
            else -> {
                // Subtle atmospheric cloud aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3394A3B8),
                            Color(0x1164748B),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.48f
                    ),
                    radius = w * 0.48f,
                    center = center
                )
            }
        }
    }
}
