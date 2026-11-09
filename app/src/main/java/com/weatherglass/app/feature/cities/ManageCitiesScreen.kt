package com.weatherglass.app.feature.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.UnitConverters
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherConditionIcon
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.WeatherCondition
import com.weatherglass.app.domain.model.WeatherReport

@Composable
fun ManageCitiesScreen(
    viewModel: ManageCitiesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onCitySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteCities by viewModel.favoriteCities.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val weatherMap by viewModel.cityWeatherMap.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
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
                horizontalArrangement = Arrangement.SpaceBetween,
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
                    text = "Saved Cities",
                    style = WeatherGlassTypography.headlineMedium,
                    color = GlassColors.TextPrimary
                )

                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add City",
                        tint = GlassColors.AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(favoriteCities, key = { _, item -> item.id }) { index, city ->
                    val report = weatherMap[city.id]
                    SavedCityCard(
                        city = city,
                        report = report,
                        temperatureUnit = preferences.temperatureUnit,
                        canDelete = favoriteCities.size > 1,
                        onClick = { onCitySelected(index) },
                        onDelete = { viewModel.removeCity(city.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun SavedCityCard(
    city: CityLocation,
    report: WeatherReport?,
    temperatureUnit: TemperatureUnit,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val condition = report?.current?.condition ?: WeatherCondition.PARTLY_CLOUDY_DAY

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0x3D1E293B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = WeatherGlassTypography.headlineMedium.copy(fontSize = 20.sp),
                    color = GlassColors.TextPrimary
                )
                val subtitle = listOf(city.region, city.country).filter { it.isNotBlank() }.joinToString(", ")
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = WeatherGlassTypography.bodyMedium.copy(fontSize = 12.sp),
                        color = GlassColors.TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WeatherConditionIcon(condition = condition, size = 20.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = report?.current?.condition?.label ?: "Loading forecast...",
                        style = WeatherGlassTypography.bodyMedium.copy(fontSize = 13.sp),
                        color = GlassColors.TextTertiary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (report != null) {
                    val temp = UnitConverters.formatTemperature(report.current.temperatureCelsius, temperatureUnit)
                    Text(
                        text = temp,
                        style = WeatherGlassTypography.displayMedium.copy(fontSize = 38.sp),
                        color = GlassColors.TextPrimary
                    )
                    val high = UnitConverters.formatTemperature(report.current.highTempCelsius, temperatureUnit)
                    val low = UnitConverters.formatTemperature(report.current.lowTempCelsius, temperatureUnit)
                    Text(
                        text = "H:$high  L:$low",
                        style = WeatherGlassTypography.labelSmall,
                        color = GlassColors.TextSecondary
                    )
                } else {
                    Text(
                        text = "--",
                        style = WeatherGlassTypography.displayMedium.copy(fontSize = 38.sp),
                        color = GlassColors.TextTertiary
                    )
                }

                if (canDelete) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete City",
                            tint = GlassColors.TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
