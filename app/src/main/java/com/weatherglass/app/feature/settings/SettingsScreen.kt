package com.weatherglass.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.designsystem.GlassCardHeader
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.LiquidGlassPill
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.AppTheme
import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.TimeFormat
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val availableProviders = viewModel.availableProviders

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GlassColors.TextPrimary
                    )
                }
                Text(
                    text = "Preferences",
                    style = WeatherGlassTypography.headlineMedium,
                    color = GlassColors.TextPrimary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Units
                item {
                    SettingsCard(
                        title = "Measurement Units",
                        icon = Icons.Default.Thermostat
                    ) {
                        UnitRow(
                            label = "Temperature",
                            options = listOf(
                                TemperatureUnit.CELSIUS.symbol to (preferences.temperatureUnit == TemperatureUnit.CELSIUS),
                                TemperatureUnit.FAHRENHEIT.symbol to (preferences.temperatureUnit == TemperatureUnit.FAHRENHEIT)
                            ),
                            onSelect = { index ->
                                val unit = if (index == 0) TemperatureUnit.CELSIUS else TemperatureUnit.FAHRENHEIT
                                viewModel.updateTemperatureUnit(unit)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        UnitRow(
                            label = "Wind Speed",
                            options = listOf(
                                "km/h" to (preferences.windSpeedUnit == WindSpeedUnit.KMH),
                                "m/s" to (preferences.windSpeedUnit == WindSpeedUnit.MS),
                                "mph" to (preferences.windSpeedUnit == WindSpeedUnit.MPH),
                                "kn" to (preferences.windSpeedUnit == WindSpeedUnit.KNOTS)
                            ),
                            onSelect = { index ->
                                val unit = when (index) {
                                    0 -> WindSpeedUnit.KMH
                                    1 -> WindSpeedUnit.MS
                                    2 -> WindSpeedUnit.MPH
                                    else -> WindSpeedUnit.KNOTS
                                }
                                viewModel.updateWindSpeedUnit(unit)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        UnitRow(
                            label = "Atmospheric Pressure",
                            options = listOf(
                                "hPa" to (preferences.pressureUnit == PressureUnit.HPA),
                                "mmHg" to (preferences.pressureUnit == PressureUnit.MMHG),
                                "inHg" to (preferences.pressureUnit == PressureUnit.INHG)
                            ),
                            onSelect = { index ->
                                val unit = when (index) {
                                    0 -> PressureUnit.HPA
                                    1 -> PressureUnit.MMHG
                                    else -> PressureUnit.INHG
                                }
                                viewModel.updatePressureUnit(unit)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        UnitRow(
                            label = "Precipitation",
                            options = listOf(
                                "mm" to (preferences.precipitationUnit == PrecipitationUnit.MILLIMETERS),
                                "in" to (preferences.precipitationUnit == PrecipitationUnit.INCHES)
                            ),
                            onSelect = { index ->
                                val unit = if (index == 0) PrecipitationUnit.MILLIMETERS else PrecipitationUnit.INCHES
                                viewModel.updatePrecipitationUnit(unit)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        UnitRow(
                            label = "Visibility",
                            options = listOf(
                                "km" to (preferences.visibilityUnit == VisibilityUnit.KILOMETERS),
                                "mi" to (preferences.visibilityUnit == VisibilityUnit.MILES)
                            ),
                            onSelect = { index ->
                                val unit = if (index == 0) VisibilityUnit.KILOMETERS else VisibilityUnit.MILES
                                viewModel.updateVisibilityUnit(unit)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        UnitRow(
                            label = "Time Format",
                            options = listOf(
                                "12-Hour" to (preferences.timeFormat == TimeFormat.TWELVE_HOUR),
                                "24-Hour" to (preferences.timeFormat == TimeFormat.TWENTY_FOUR_HOUR)
                            ),
                            onSelect = { index ->
                                val format = if (index == 0) TimeFormat.TWELVE_HOUR else TimeFormat.TWENTY_FOUR_HOUR
                                viewModel.updateTimeFormat(format)
                            }
                        )
                    }
                }

                // Section 2: Appearance & Motion
                item {
                    SettingsCard(
                        title = "Appearance & Performance",
                        icon = Icons.Default.Palette
                    ) {
                        UnitRow(
                            label = "Theme",
                            options = listOf(
                                "Dark" to (preferences.theme == AppTheme.DARK),
                                "Light" to (preferences.theme == AppTheme.LIGHT),
                                "System" to (preferences.theme == AppTheme.SYSTEM)
                            ),
                            onSelect = { index ->
                                val theme = when (index) {
                                    0 -> AppTheme.DARK
                                    1 -> AppTheme.LIGHT
                                    else -> AppTheme.SYSTEM
                                }
                                viewModel.updateTheme(theme)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Atmospheric Particle Animations",
                                    style = WeatherGlassTypography.bodyLarge,
                                    color = GlassColors.TextPrimary
                                )
                                Text(
                                    text = "Rain, snow, and celestial animations (disable to save battery)",
                                    style = WeatherGlassTypography.labelSmall,
                                    color = GlassColors.TextTertiary
                                )
                            }
                            Switch(
                                checked = preferences.animationsEnabled,
                                onCheckedChange = { viewModel.updateAnimations(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GlassColors.AccentCyan
                                )
                            )
                        }
                    }
                }

                // Section 3: Weather Data Providers & Developer Diagnostics
                item {
                    SettingsCard(
                        title = "Meteorological Provider & Diagnostics",
                        icon = Icons.Default.CloudQueue
                    ) {
                        Text(
                            text = "Selected Provider",
                            style = WeatherGlassTypography.labelLarge,
                            color = GlassColors.TextTertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        availableProviders.forEach { provider ->
                            val isSelected = provider.id == preferences.selectedProviderId
                            ProviderSelectionTile(
                                provider = provider,
                                isSelected = isSelected,
                                onClick = { viewModel.updateSelectedProvider(provider.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Security Architecture Callout
                        LiquidGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = Color(0x280284C7)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = GlassColors.AccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text(
                                        text = "PRODUCTION SECURITY NOTICE",
                                        style = WeatherGlassTypography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                                        color = GlassColors.AccentCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Open-Meteo runs zero-secret without keys. For secret-key based providers (WeatherAPI, OpenWeather), secrets should be secured behind a server-side proxy rather than shipped inside client APKs where local.properties or BuildConfig can be decompiled.",
                                    style = WeatherGlassTypography.labelSmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                    color = GlassColors.TextSecondary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            GlassCardHeader(title = title, icon = icon)
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnitRow(
    label: String,
    options: List<Pair<String, Boolean>>,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = label,
            style = WeatherGlassTypography.bodyMedium,
            color = GlassColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEachIndexed { index, (text, isSelected) ->
                LiquidGlassPill(
                    text = text,
                    isActive = isSelected,
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun ProviderSelectionTile(
    provider: WeatherProvider,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = if (isSelected) Color(0x3838BDF8) else Color(0x1F1E293B),
        borderColor = if (isSelected) GlassColors.AccentCyan else GlassColors.GlassBorderSubtle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provider.displayName,
                    style = WeatherGlassTypography.titleLarge.copy(fontSize = 15.sp),
                    color = GlassColors.TextPrimary
                )
                Text(
                    text = provider.description,
                    style = WeatherGlassTypography.labelSmall.copy(fontSize = 11.sp),
                    color = GlassColors.TextTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val caps = listOfNotNull(
                    if (provider.capabilities.supportsCurrent) "Current" else null,
                    if (provider.capabilities.supportsHourly) "Hourly" else null,
                    if (provider.capabilities.supportsDaily) "7-Day" else null,
                    if (provider.capabilities.supportsAirQuality) "Air Quality" else null
                ).joinToString(" • ")
                Text(
                    text = caps,
                    style = WeatherGlassTypography.labelSmall.copy(fontSize = 10.sp, color = GlassColors.AccentCyan)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = GlassColors.AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
