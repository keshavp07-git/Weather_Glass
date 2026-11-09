package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.SunMoon

@Composable
fun SunMoonCard(
    sunMoon: SunMoon,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Sun & Moon",
                icon = Icons.Default.BrightnessMedium
            )

            // Sunrise / Sunset Arch Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    val w = size.width
                    val h = size.height

                    // Baseline
                    drawLine(
                        color = GlassColors.GlassBorderSubtle,
                        start = Offset(w * 0.1f, h * 0.85f),
                        end = Offset(w * 0.9f, h * 0.85f),
                        strokeWidth = 1.5f
                    )

                    // Solar Arch
                    val archPath = Path().apply {
                        moveTo(w * 0.15f, h * 0.85f)
                        quadraticTo(w * 0.5f, -h * 0.2f, w * 0.85f, h * 0.85f)
                    }

                    drawPath(
                        path = archPath,
                        color = Color(0x66FFD166),
                        style = Stroke(
                            width = 2.5f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                        )
                    )

                    // Sun Position dot (approx midway)
                    drawCircle(
                        color = Color(0xFFFFD166),
                        radius = 6.dp.toPx(),
                        center = Offset(w * 0.5f, h * 0.32f)
                    )
                }
            }

            // Sunrise & Sunset labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = GlassColors.AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Sunrise",
                            style = WeatherGlassTypography.labelSmall,
                            color = GlassColors.TextTertiary
                        )
                    }
                    Text(
                        text = sunMoon.sunriseTime,
                        style = WeatherGlassTypography.titleLarge.copy(fontSize = 17.sp),
                        color = GlassColors.TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sunset",
                            style = WeatherGlassTypography.labelSmall,
                            color = GlassColors.TextTertiary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = GlassColors.AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = sunMoon.sunsetTime,
                        style = WeatherGlassTypography.titleLarge.copy(fontSize = 17.sp),
                        color = GlassColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = GlassColors.GlassBorderSubtle,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Moon details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Moon Phase",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextTertiary
                    )
                    Text(
                        text = sunMoon.moonPhaseName,
                        style = WeatherGlassTypography.bodyLarge.copy(fontSize = 15.sp),
                        color = GlassColors.TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Illumination",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextTertiary
                    )
                    Text(
                        text = "${sunMoon.moonIlluminationPercent}%",
                        style = WeatherGlassTypography.titleLarge.copy(fontSize = 16.sp),
                        color = GlassColors.AccentIndigo
                    )
                }
            }
        }
    }
}
