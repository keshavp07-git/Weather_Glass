package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherConditionIcon
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.DailyForecast
import com.weatherglass.app.domain.model.TemperatureUnit

@Composable
fun DailyForecastCard(
    dailyList: List<DailyForecast>,
    temperatureUnit: TemperatureUnit,
    onDayClick: (DailyForecast) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dailyList.isEmpty()) return

    val weekMin = dailyList.minOfOrNull { it.minTempCelsius } ?: 0.0
    val weekMax = dailyList.maxOfOrNull { it.maxTempCelsius } ?: 30.0
    val weekRange = (weekMax - weekMin).coerceAtLeast(1.0)

    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "7-Day Forecast",
                icon = Icons.Default.CalendarMonth
            )

            dailyList.forEachIndexed { index, day ->
                DailyForecastRow(
                    day = day,
                    weekMin = weekMin,
                    weekRange = weekRange,
                    temperatureUnit = temperatureUnit,
                    onClick = { onDayClick(day) }
                )
                if (index < dailyList.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                            drawLine(
                                color = GlassColors.GlassBorderSubtle,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1f
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRow(
    day: DailyForecast,
    weekMin: Double,
    weekRange: Double,
    temperatureUnit: TemperatureUnit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day name
        Text(
            text = day.dayName,
            style = WeatherGlassTypography.bodyLarge.copy(fontSize = 15.sp),
            color = GlassColors.TextPrimary,
            modifier = Modifier.width(68.dp)
        )

        // Weather icon + rain prob
        Row(
            modifier = Modifier.width(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherConditionIcon(
                condition = day.condition,
                size = 24.dp
            )
            if (day.precipitationProbabilityPercent > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${day.precipitationProbabilityPercent}%",
                    style = WeatherGlassTypography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = GlassColors.AccentCyan
                    )
                )
            }
        }

        // Min Temp
        val minFormatted = UnitConverters.formatTemperature(day.minTempCelsius, temperatureUnit)
        Text(
            text = minFormatted,
            style = WeatherGlassTypography.bodyMedium.copy(fontSize = 14.sp),
            color = GlassColors.TextSecondary,
            modifier = Modifier.width(36.dp)
        )

        // Temperature Gradient Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .padding(horizontal = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
                val totalWidth = size.width
                val barHeight = size.height

                // Background track
                drawRoundRect(
                    color = Color(0x330F172A),
                    size = Size(totalWidth, barHeight),
                    cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
                )

                // Normalized start and end
                val startFraction = ((day.minTempCelsius - weekMin) / weekRange).toFloat().coerceIn(0f, 1f)
                val endFraction = ((day.maxTempCelsius - weekMin) / weekRange).toFloat().coerceIn(0f, 1f)

                val barStart = startFraction * totalWidth
                val barWidth = ((endFraction - startFraction) * totalWidth).coerceAtLeast(barHeight)

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GlassColors.AccentTeal, GlassColors.AccentGold),
                        startX = barStart,
                        endX = barStart + barWidth
                    ),
                    topLeft = Offset(barStart, 0f),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
                )
            }
        }

        // Max Temp
        val maxFormatted = UnitConverters.formatTemperature(day.maxTempCelsius, temperatureUnit)
        Text(
            text = maxFormatted,
            style = WeatherGlassTypography.bodyLarge.copy(fontSize = 14.sp),
            color = GlassColors.TextPrimary,
            modifier = Modifier.width(36.dp)
        )
    }
}
