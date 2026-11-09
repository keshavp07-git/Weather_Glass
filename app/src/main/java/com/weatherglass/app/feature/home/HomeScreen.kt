package com.weatherglass.app.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherglass.app.core.common.DateTimeFormatters
import com.weatherglass.app.core.designsystem.AtmosphericBackground
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.WeatherCondition
import com.weatherglass.app.domain.model.WeatherReport
import com.weatherglass.app.feature.home.components.AirQualityCard
import com.weatherglass.app.feature.home.components.DailyForecastCard
import com.weatherglass.app.feature.home.components.HourlyForecastCard
import com.weatherglass.app.feature.home.components.MainTemperatureSection
import com.weatherglass.app.feature.home.components.OfflineStatusBanner
import com.weatherglass.app.feature.home.components.SunMoonCard
import com.weatherglass.app.feature.home.components.TopBarSection
import com.weatherglass.app.feature.home.components.WeatherOverviewGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToManageCities: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetails: (CityLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferences by viewModel.preferences.collectAsState()

    val cities = uiState.cities
    if (cities.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GlassColors.AccentCyan)
        }
        return
    }

    val safeInitialPage = uiState.currentCityIndex.coerceIn(0, cities.size - 1)
    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { cities.size }
    )

    // User manual swipe synchronization
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != uiState.currentCityIndex) {
                viewModel.onPageChanged(page)
            }
        }
    }

    // Programmatic selection synchronization (e.g. from Search or Saved Cities)
    LaunchedEffect(uiState.currentCityIndex) {
        if (uiState.currentCityIndex in 0 until cities.size && pagerState.currentPage != uiState.currentCityIndex) {
            pagerState.animateScrollToPage(uiState.currentCityIndex)
        }
    }

    val currentPageIndex = pagerState.currentPage.coerceIn(0, cities.size - 1)
    val currentCity = cities[currentPageIndex]
    val currentReport = uiState.weatherReports[currentCity.id]
    val currentCondition = currentReport?.current?.condition ?: WeatherCondition.PARTLY_CLOUDY_DAY

    AtmosphericBackground(
        condition = currentCondition,
        animationsEnabled = preferences.animationsEnabled,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            val nowEpoch = System.currentTimeMillis() / 1000
            val formattedDate = DateTimeFormatters.formatFullDate(nowEpoch)

            TopBarSection(
                location = currentCity,
                currentDateTimeFormatted = formattedDate,
                pageCount = cities.size,
                currentPage = currentPageIndex,
                onSearchClick = onNavigateToSearch,
                onManageCitiesClick = onNavigateToManageCities,
                onSettingsClick = onNavigateToSettings
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val pageCity = cities.getOrNull(pageIndex) ?: currentCity
                val pageReport = uiState.weatherReports[pageCity.id]

                CityWeatherPageContent(
                    city = pageCity,
                    report = pageReport,
                    preferences = preferences,
                    isLoading = uiState.isLoading && pageReport == null,
                    errorMessage = uiState.errorMessage,
                    onRetry = { viewModel.loadWeatherForCity(pageCity, forceRefresh = true) },
                    onDetailsClick = { onNavigateToDetails(pageCity) }
                )
            }
        }
    }
}

@Composable
private fun CityWeatherPageContent(
    city: CityLocation,
    report: WeatherReport?,
    preferences: UserPreferences,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onDetailsClick: () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GlassColors.AccentCyan)
        }
        return
    }

    if (errorMessage != null && report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = errorMessage,
                    style = WeatherGlassTypography.bodyLarge,
                    color = GlassColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Try Again")
                }
            }
        }
        return
    }

    if (report == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OfflineStatusBanner(
                lastUpdatedEpochSeconds = report.lastUpdatedEpochSeconds,
                isCached = report.isCached,
                onRefresh = onRetry
            )
        }

        item {
            MainTemperatureSection(
                current = report.current,
                temperatureUnit = preferences.temperatureUnit,
                onDetailsClick = onDetailsClick
            )
        }

        item {
            HourlyForecastCard(
                hourlyList = report.hourly,
                temperatureUnit = preferences.temperatureUnit
            )
        }

        item {
            DailyForecastCard(
                dailyList = report.daily,
                temperatureUnit = preferences.temperatureUnit,
                onDayClick = { onDetailsClick() }
            )
        }

        item {
            WeatherOverviewGrid(
                current = report.current,
                temperatureUnit = preferences.temperatureUnit,
                windSpeedUnit = preferences.windSpeedUnit,
                pressureUnit = preferences.pressureUnit,
                precipitationUnit = preferences.precipitationUnit,
                visibilityUnit = preferences.visibilityUnit
            )
        }

        item {
            AirQualityCard(airQuality = report.airQuality)
        }

        item {
            SunMoonCard(sunMoon = report.sunMoon)
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
