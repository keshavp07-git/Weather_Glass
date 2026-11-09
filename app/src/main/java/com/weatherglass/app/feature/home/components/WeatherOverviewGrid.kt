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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit

@Composable
fun WeatherOverviewGrid(
    current: CurrentWeather,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    pressureUnit: PressureUnit,
    precipitationUnit: PrecipitationUnit,
    visibilityUnit: VisibilityUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: UV Index & Wind
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // UV Index
            val uvDesc = when {
                current.uvIndex < 3 -> "Low"
                current.uvIndex < 6 -> "Moderate"
                current.uvIndex < 8 -> "High"
                current.uvIndex < 11 -> "Very High"
                else -> "Extreme"
            }
            OverviewTile(
                title = "UV Index",
                icon = Icons.Default.WbSunny,
                value = String.format("%.1f", current.uvIndex),
                subtitle = uvDesc,
                footnote = if (current.uvIndex >= 6) "Protection needed" else "Minimal risk",
                modifier = Modifier.weight(1f)
            )

            // Wind
            val windSpeedFormatted = UnitConverters.formatWindSpeed(current.windSpeedKmh, windSpeedUnit)
            val cardinal = UnitConverters.degreesToCardinal(current.windDirectionDegrees)
            OverviewTile(
                title = "Wind",
                icon = Icons.Default.Air,
                value = windSpeedFormatted,
                subtitle = "From $cardinal (${current.windDirectionDegrees}°)",
                footnote = "Gusts up to ${UnitConverters.formatWindSpeed(current.windGustsKmh, windSpeedUnit)}",
                trailing = {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = GlassColors.AccentCyan,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(current.windDirectionDegrees.toFloat())
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Humidity & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Humidity & Dew Point
            val dewPointFormatted = UnitConverters.formatTemperature(current.dewPointCelsius, temperatureUnit)
            OverviewTile(
                title = "Humidity",
                icon = Icons.Default.WaterDrop,
                value = "${current.humidityPercent}%",
                subtitle = "Dew point: $dewPointFormatted",
                footnote = if (current.humidityPercent > 70) "Humid & damp" else "Comfortable range",
                modifier = Modifier.weight(1f)
            )

            // Pressure
            val pressureFormatted = UnitConverters.formatPressure(current.pressureHpa, pressureUnit)
            OverviewTile(
                title = "Pressure",
                icon = Icons.Default.Compress,
                value = pressureFormatted,
                subtitle = if (current.pressureHpa > 1013) "High pressure" else "Low pressure",
                footnote = "Stable atmosphere",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Visibility & Precipitation/Cloud
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Visibility
            val visibilityFormatted = UnitConverters.formatVisibility(current.visibilityKm, visibilityUnit)
            OverviewTile(
                title = "Visibility",
                icon = Icons.Default.Visibility,
                value = visibilityFormatted,
                subtitle = if (current.visibilityKm >= 10.0) "Perfect clear view" else "Slight atmospheric haze",
                footnote = "Normal conditions",
                modifier = Modifier.weight(1f)
            )

            // Clouds & Rain
            val precipFormatted = UnitConverters.formatPrecipitation(current.precipitationMm, precipitationUnit)
            OverviewTile(
                title = "Cloud & Rain",
                icon = Icons.Default.Cloud,
                value = "${current.cloudCoverPercent}%",
                subtitle = "Precip: $precipFormatted",
                footnote = if (current.rainProbabilityPercent > 0) "${current.rainProbabilityPercent}% chance of rain" else "Dry conditions",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewTile(
    title: String,
    icon: ImageVector,
    value: String,
    subtitle: String,
    footnote: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GlassColors.TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = title.uppercase(),
                        style = WeatherGlassTypography.labelSmall.copy(letterSpacing = 0.8.sp),
                        color = GlassColors.TextTertiary
                    )
                }
                trailing?.invoke()
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = WeatherGlassTypography.headlineMedium.copy(fontSize = 24.sp),
                color = GlassColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = WeatherGlassTypography.bodyMedium.copy(fontSize = 13.sp),
                color = GlassColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = footnote,
                style = WeatherGlassTypography.labelSmall.copy(fontSize = 11.sp),
                color = GlassColors.TextTertiary
            )
        }
    }
}
