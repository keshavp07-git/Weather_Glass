package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.WeatherConditionIcon
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.TemperatureUnit

@Composable
fun MainTemperatureSection(
    current: CurrentWeather,
    temperatureUnit: TemperatureUnit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailsClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Crisp Temperature Text (No overlapping graphics behind it)
        val tempFormatted = UnitConverters.formatTemperature(current.temperatureCelsius, temperatureUnit)
        Text(
            text = tempFormatted,
            style = WeatherGlassTypography.displayLarge.copy(
                fontSize = 86.sp,
                letterSpacing = (-2).sp
            ),
            color = GlassColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Weather Condition with dedicated inline Weather Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WeatherConditionIcon(
                condition = current.condition,
                size = 26.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = current.condition.label,
                style = WeatherGlassTypography.titleLarge,
                color = GlassColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Feels Like • High / Low
        val feelsLikeFormatted = UnitConverters.formatTemperature(current.feelsLikeCelsius, temperatureUnit)
        val highFormatted = UnitConverters.formatTemperature(current.highTempCelsius, temperatureUnit)
        val lowFormatted = UnitConverters.formatTemperature(current.lowTempCelsius, temperatureUnit)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Feels like $feelsLikeFormatted",
                style = WeatherGlassTypography.bodyMedium,
                color = GlassColors.TextSecondary
            )
            Text(
                text = "  •  ",
                style = WeatherGlassTypography.bodyMedium,
                color = GlassColors.TextTertiary
            )
            Text(
                text = "H: $highFormatted  L: $lowFormatted",
                style = WeatherGlassTypography.bodyMedium,
                color = GlassColors.TextSecondary
            )
        }

        if (current.conditionDescription.isNotBlank() && current.conditionDescription != current.condition.label) {
            Text(
                text = current.conditionDescription,
                style = WeatherGlassTypography.labelSmall,
                color = GlassColors.TextTertiary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
