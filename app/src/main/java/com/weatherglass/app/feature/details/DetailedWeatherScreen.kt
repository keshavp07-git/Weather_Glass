package com.weatherglass.app.feature.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.AtmosphericBackground
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.WeatherCondition
import com.weatherglass.app.domain.model.WeatherReport
import com.weatherglass.app.feature.home.components.AirQualityCard
import com.weatherglass.app.feature.home.components.DailyForecastCard
import com.weatherglass.app.feature.home.components.HourlyForecastCard
import com.weatherglass.app.feature.home.components.SunMoonCard

@Composable
fun DetailedWeatherScreen(
    city: CityLocation,
    viewModel: DetailedWeatherViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val report = uiState.report

    val condition = report?.current?.condition ?: WeatherCondition.PARTLY_CLOUDY_DAY

    AtmosphericBackground(
        condition = condition,
        animationsEnabled = preferences.animationsEnabled,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GlassColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = city.name,
                        style = WeatherGlassTypography.headlineMedium,
                        color = GlassColors.TextPrimary
                    )
                    Text(
                        text = "Detailed Weather Report",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextSecondary
                    )
                }
            }

            if (uiState.isLoading && report == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlassColors.AccentCyan)
                }
                return@Column
            }

            if (report == null) return@Column

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Conditions Overview Summary
                item {
                    CurrentConditionsSummaryCard(
                        current = report.current,
                        preferences = preferences
                    )
                }

                // Section 2: Detailed Hourly Forecast
                item {
                    HourlyForecastCard(
                        hourlyList = report.hourly,
                        temperatureUnit = preferences.temperatureUnit
                    )
                }

                // Section 3: 7-Day Daily Forecast
                item {
                    DailyForecastCard(
                        dailyList = report.daily,
                        temperatureUnit = preferences.temperatureUnit,
                        onDayClick = {}
                    )
                }

                // Section 4: Precipitation & Rainfall Timeline
                item {
                    PrecipitationTimelineCard(
                        current = report.current,
                        preferences = preferences
                    )
                }

                // Section 5: Wind & Compass Visualization
                item {
                    WindCompassCard(
                        current = report.current,
                        preferences = preferences
                    )
                }

                // Section 6: Air Quality
                item {
                    AirQualityCard(airQuality = report.airQuality)
                }

                // Section 7: Astronomy
                item {
                    SunMoonCard(sunMoon = report.sunMoon)
                }

                // Section 8: Weather Alerts
                item {
                    WeatherAlertsCard(alerts = report.alerts)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun CurrentConditionsSummaryCard(
    current: CurrentWeather,
    preferences: UserPreferences
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Current Conditions",
                icon = Icons.Default.Info
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val temp = UnitConverters.formatTemperature(current.temperatureCelsius, preferences.temperatureUnit)
                    Text(
                        text = temp,
                        style = WeatherGlassTypography.displayLarge.copy(fontSize = 52.sp),
                        color = GlassColors.TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.weatherglass.app.core.designsystem.WeatherConditionIcon(condition = current.condition, size = 22.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = current.conditionDescription,
                            style = WeatherGlassTypography.titleLarge,
                            color = GlassColors.TextPrimary
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    val feelsLike = UnitConverters.formatTemperature(current.feelsLikeCelsius, preferences.temperatureUnit)
                    Text(
                        text = "Feels like $feelsLike",
                        style = WeatherGlassTypography.bodyLarge,
                        color = GlassColors.TextSecondary
                    )
                    val high = UnitConverters.formatTemperature(current.highTempCelsius, preferences.temperatureUnit)
                    val low = UnitConverters.formatTemperature(current.lowTempCelsius, preferences.temperatureUnit)
                    Text(
                        text = "High: $high • Low: $low",
                        style = WeatherGlassTypography.bodyMedium,
                        color = GlassColors.TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun PrecipitationTimelineCard(
    current: CurrentWeather,
    preferences: UserPreferences
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Rain & Precipitation",
                icon = Icons.Default.WaterDrop
            )

            val amount = UnitConverters.formatPrecipitation(current.precipitationMm, preferences.precipitationUnit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = amount,
                        style = WeatherGlassTypography.headlineLarge,
                        color = GlassColors.TextPrimary
                    )
                    Text(
                        text = "Accumulated today",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextTertiary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${current.rainProbabilityPercent}%",
                        style = WeatherGlassTypography.headlineLarge,
                        color = GlassColors.AccentCyan
                    )
                    Text(
                        text = "Rain Probability",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (current.precipitationMm > 0.0) {
                    "Precipitation detected in the current atmospheric monitoring cycle."
                } else {
                    "No rain or snowfall expected for the next monitoring window."
                },
                style = WeatherGlassTypography.bodyMedium.copy(fontSize = 13.sp),
                color = GlassColors.TextSecondary
            )
        }
    }
}

@Composable
private fun WindCompassCard(
    current: CurrentWeather,
    preferences: UserPreferences
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Wind & Airflow",
                icon = Icons.Default.Air
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val speed = UnitConverters.formatWindSpeed(current.windSpeedKmh, preferences.windSpeedUnit)
                    Text(
                        text = speed,
                        style = WeatherGlassTypography.headlineLarge,
                        color = GlassColors.TextPrimary
                    )
                    val cardinal = UnitConverters.degreesToCardinal(current.windDirectionDegrees)
                    Text(
                        text = "Blowing from $cardinal (${current.windDirectionDegrees}°)",
                        style = WeatherGlassTypography.bodyMedium,
                        color = GlassColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val gusts = UnitConverters.formatWindSpeed(current.windGustsKmh, preferences.windSpeedUnit)
                    Text(
                        text = "Peak Gusts: $gusts",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextTertiary
                    )
                }

                // Wind compass visualizer
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val r = w * 0.45f
                        val center = Offset(w * 0.5f, w * 0.5f)

                        // Outer ring
                        drawCircle(
                            color = GlassColors.GlassBorderLight,
                            radius = r,
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Cardinal tick marks
                        val cardinals = listOf("N", "E", "S", "W")
                        for (i in 0 until 4) {
                            val angle = i * Math.PI / 2
                            val tickStart = Offset(
                                (center.x + (r - 6.dp.toPx()) * cos(angle)).toFloat(),
                                (center.y + (r - 6.dp.toPx()) * sin(angle)).toFloat()
                            )
                            val tickEnd = Offset(
                                (center.x + r * cos(angle)).toFloat(),
                                (center.y + r * sin(angle)).toFloat()
                            )
                            drawLine(
                                color = Color.White,
                                start = tickStart,
                                end = tickEnd,
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Rotating Arrow
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Wind Arrow",
                        tint = GlassColors.AccentCyan,
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(current.windDirectionDegrees.toFloat())
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherAlertsCard(
    alerts: List<com.weatherglass.app.domain.model.WeatherAlert>
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(
                title = "Official Alerts & Advisories",
                icon = Icons.Default.Warning
            )

            if (alerts.isEmpty()) {
                Text(
                    text = "No severe weather alerts active for this region. Atmospheric conditions are currently within normal baseline thresholds.",
                    style = WeatherGlassTypography.bodyMedium.copy(fontSize = 13.sp),
                    color = GlassColors.TextSecondary
                )
            } else {
                alerts.forEach { alert ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = alert.title,
                            style = WeatherGlassTypography.titleLarge.copy(fontSize = 16.sp),
                            color = GlassColors.AccentRose
                        )
                        Text(
                            text = alert.description,
                            style = WeatherGlassTypography.bodyMedium,
                            color = GlassColors.TextPrimary
                        )
                        if (alert.issuingAuthority.isNotBlank()) {
                            Text(
                                text = "Issued by: ${alert.issuingAuthority}",
                                style = WeatherGlassTypography.labelSmall,
                                color = GlassColors.TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun cos(rad: Double) = kotlin.math.cos(rad)
private fun sin(rad: Double) = kotlin.math.sin(rad)
