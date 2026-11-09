package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.AirQuality

@Composable
fun AirQualityCard(
    airQuality: AirQuality?,
    modifier: Modifier = Modifier
) {
    if (airQuality == null) return

    val categoryColor = Color(airQuality.category.hexColor)

    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Air Quality Index",
                icon = Icons.Default.Air,
                trailingText = "AQI: ${airQuality.aqi}"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${airQuality.aqi}",
                        style = WeatherGlassTypography.displayMedium.copy(fontSize = 36.sp),
                        color = GlassColors.TextPrimary
                    )
                    Text(
                        text = airQuality.category.label,
                        style = WeatherGlassTypography.titleLarge.copy(fontSize = 18.sp),
                        color = categoryColor
                    )
                }

                // Category pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = airQuality.category.label.uppercase(),
                        style = WeatherGlassTypography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = categoryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-color AQI spectrum bar with current indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
                    val w = size.width
                    val h = size.height

                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFFFFC107),
                            Color(0xFFFF9800),
                            Color(0xFFF44336),
                            Color(0xFF9C27B0)
                        )
                    )
                    drawRoundRect(
                        brush = gradient,
                        size = Size(w, h),
                        cornerRadius = CornerRadius(h / 2, h / 2)
                    )

                    // Current pointer across standard 0-500 scale
                    val fraction = (airQuality.aqi / 500f).coerceIn(0.02f, 0.98f)
                    val pointerX = fraction * w
                    drawCircle(
                        color = Color.White,
                        radius = h * 0.9f,
                        center = Offset(pointerX, h / 2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Health guidance
            Text(
                text = airQuality.category.healthAdvice,
                style = WeatherGlassTypography.bodyMedium.copy(fontSize = 13.sp),
                color = GlassColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Pollutants breakdown grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PollutantItem("PM2.5", String.format("%.1f", airQuality.pm25))
                PollutantItem("PM10", String.format("%.1f", airQuality.pm10))
                PollutantItem("O₃", String.format("%.1f", airQuality.o3))
                PollutantItem("NO₂", String.format("%.1f", airQuality.no2))
                PollutantItem("SO₂", String.format("%.1f", airQuality.so2))
            }
        }
    }
}

@Composable
private fun PollutantItem(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            style = WeatherGlassTypography.labelSmall.copy(color = GlassColors.TextTertiary)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = WeatherGlassTypography.bodyMedium.copy(
                fontSize = 13.sp,
                color = GlassColors.TextPrimary
            )
        )
    }
}
