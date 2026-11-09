package com.weatherglass.app.core.designsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.weatherglass.app.domain.model.WeatherCondition
import kotlin.random.Random

private class Star(val xFraction: Float, val yFraction: Float, val radius: Float, val phaseOffset: Float)
private class Raindrop(val xFraction: Float, val speed: Float, val length: Float, val alpha: Float)
private class Snowflake(val xFraction: Float, val speed: Float, val radius: Float, val driftPhase: Float)

@Composable
fun AtmosphericBackground(
    condition: WeatherCondition,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientColors = when (condition) {
        WeatherCondition.CLEAR_DAY -> GlassColors.ClearDayGradient
        WeatherCondition.CLEAR_NIGHT -> GlassColors.ClearNightGradient
        WeatherCondition.PARTLY_CLOUDY_DAY -> GlassColors.PartlyCloudyDayGradient
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> GlassColors.PartlyCloudyNightGradient
        WeatherCondition.OVERCAST -> GlassColors.OvercastGradient
        WeatherCondition.LIGHT_DRIZZLE, WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> GlassColors.RainGradient
        WeatherCondition.THUNDERSTORM -> GlassColors.ThunderstormGradient
        WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW -> GlassColors.SnowGradient
        WeatherCondition.FOG -> GlassColors.FogGradient
        WeatherCondition.WINDY -> GlassColors.PartlyCloudyDayGradient
    }

    val infiniteTransition = rememberInfiniteTransition(label = "AtmosphereAnimation")

    val animProgress by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particles"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val lightningAlpha by if (animationsEnabled && condition == WeatherCondition.THUNDERSTORM) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "lightning"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Stable pre-generated particles
    val stars = remember {
        List(40) {
            Star(
                xFraction = Random.nextFloat(),
                yFraction = Random.nextFloat() * 0.7f,
                radius = Random.nextFloat() * 1.6f + 0.6f,
                phaseOffset = Random.nextFloat()
            )
        }
    }

    val raindrops = remember {
        List(50) {
            Raindrop(
                xFraction = Random.nextFloat(),
                speed = Random.nextFloat() * 0.6f + 0.7f,
                length = Random.nextFloat() * 22f + 16f,
                alpha = Random.nextFloat() * 0.4f + 0.2f
            )
        }
    }

    val snowflakes = remember {
        List(40) {
            Snowflake(
                xFraction = Random.nextFloat(),
                speed = Random.nextFloat() * 0.3f + 0.2f,
                radius = Random.nextFloat() * 2.2f + 1.2f,
                driftPhase = Random.nextFloat() * 6.28f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    ) {
        // Dynamic Canvas Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (condition) {
                WeatherCondition.CLEAR_NIGHT, WeatherCondition.PARTLY_CLOUDY_NIGHT -> {
                    drawStars(stars, width, height, animProgress)
                }
                WeatherCondition.LIGHT_DRIZZLE, WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> {
                    drawRain(raindrops, width, height, animProgress)
                }
                WeatherCondition.THUNDERSTORM -> {
                    drawRain(raindrops, width, height, animProgress)
                    // Restrained flash
                    if (lightningAlpha in 0.88f..0.93f) {
                        drawRect(color = Color.White.copy(alpha = 0.16f))
                    }
                }
                WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW -> {
                    drawSnow(snowflakes, width, height, animProgress)
                }
                WeatherCondition.CLEAR_DAY -> {
                    // Soft radiant sun glow top right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x33FFD166), Color.Transparent),
                            center = Offset(width * 0.85f, height * 0.15f),
                            radius = width * 0.5f
                        ),
                        center = Offset(width * 0.85f, height * 0.15f),
                        radius = width * 0.5f
                    )
                }
                else -> {
                    // Soft atmospheric light wash
                }
            }
        }

        content()
    }
}

private fun DrawScope.drawStars(stars: List<Star>, width: Float, height: Float, progress: Float) {
    for (star in stars) {
        val twinkle = ((progress + star.phaseOffset) % 1f)
        val alpha = if (twinkle < 0.5f) 0.3f + twinkle * 1.4f else 1.0f - (twinkle - 0.5f) * 1.4f
        drawCircle(
            color = Color.White.copy(alpha = alpha.coerceIn(0.2f, 0.95f)),
            radius = star.radius,
            center = Offset(star.xFraction * width, star.yFraction * height)
        )
    }
}

private fun DrawScope.drawRain(raindrops: List<Raindrop>, width: Float, height: Float, progress: Float) {
    for (drop in raindrops) {
        val y = ((progress * drop.speed * 2f + drop.xFraction) % 1f) * height
        val x = drop.xFraction * width - (y * 0.1f) // slight angle
        drawLine(
            color = Color(0xB3E2E8F0).copy(alpha = drop.alpha),
            start = Offset(x, y),
            end = Offset(x - 3f, y + drop.length),
            strokeWidth = 1.4f
        )
    }
}

private fun DrawScope.drawSnow(snowflakes: List<Snowflake>, width: Float, height: Float, progress: Float) {
    for (flake in snowflakes) {
        val y = ((progress * flake.speed + flake.xFraction) % 1f) * height
        val drift = kotlin.math.sin(progress * 6.28f + flake.driftPhase).toFloat() * 14f
        val x = (flake.xFraction * width + drift) % width
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = flake.radius,
            center = Offset(x, y)
        )
    }
}
