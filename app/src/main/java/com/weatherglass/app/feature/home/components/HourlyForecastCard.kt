package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherConditionIcon
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.HourlyForecast
import com.weatherglass.app.domain.model.TemperatureUnit

@Composable
fun HourlyForecastCard(
    hourlyList: List<HourlyForecast>,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 14.dp)) {
            GlassCardHeader(
                title = "Hourly Forecast",
                icon = Icons.Default.Schedule,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(hourlyList, key = { it.epochSeconds }) { hourly ->
                    HourlyItem(
                        hourly = hourly,
                        temperatureUnit = temperatureUnit
                    )
                }
            }
        }
    }
}

@Composable
private fun HourlyItem(
    hourly: HourlyForecast,
    temperatureUnit: TemperatureUnit
) {
    val isNow = hourly.isNow
    val bgModifier = if (isNow) {
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x3D38BDF8))
            .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    } else {
        Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
    }

    Column(
        modifier = bgModifier.width(52.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hourly.formattedTime,
            style = WeatherGlassTypography.labelSmall.copy(
                fontSize = 12.sp,
                color = if (isNow) GlassColors.TextPrimary else GlassColors.TextSecondary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        WeatherConditionIcon(
            condition = hourly.condition,
            size = 28.dp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Precipitation probability if > 0
        if (hourly.precipitationProbabilityPercent > 0) {
            Text(
                text = "${hourly.precipitationProbabilityPercent}%",
                style = WeatherGlassTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = GlassColors.AccentCyan
                )
            )
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        val tempText = UnitConverters.formatTemperature(hourly.temperatureCelsius, temperatureUnit)
        Text(
            text = tempText,
            style = WeatherGlassTypography.titleMedium.copy(
                fontSize = 15.sp,
                color = GlassColors.TextPrimary
            )
        )
    }
}
