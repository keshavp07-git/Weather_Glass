package com.weatherglass.app.data.weather

import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.common.DispatcherProvider
import com.weatherglass.app.core.database.dao.WeatherCacheDao
import com.weatherglass.app.core.database.entity.WeatherCacheEntity
import com.weatherglass.app.data.weather.provider.ProviderCapabilities
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.AirQuality
import com.weatherglass.app.domain.model.AirQualityCategory
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.SunMoon
import com.weatherglass.app.domain.model.WeatherCondition
import com.weatherglass.app.domain.model.WeatherReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testDispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    private val dummyLocation = CityLocation(
        id = "test_city",
        name = "Test City",
        region = "Test Region",
        country = "Test Country",
        latitude = 10.0,
        longitude = 20.0
    )

    private val dummyReport = WeatherReport(
        location = dummyLocation,
        current = CurrentWeather(
            temperatureCelsius = 22.0,
            feelsLikeCelsius = 23.0,
            condition = WeatherCondition.CLEAR_DAY,
            conditionDescription = "Clear",
            highTempCelsius = 25.0,
            lowTempCelsius = 18.0,
            humidityPercent = 50,
            windSpeedKmh = 10.0,
            windDirectionDegrees = 180,
            windGustsKmh = 15.0,
            pressureHpa = 1012.0,
            visibilityKm = 10.0,
            cloudCoverPercent = 10,
            dewPointCelsius = 12.0,
            uvIndex = 4.0,
            precipitationMm = 0.0,
            rainProbabilityPercent = 0
        ),
        hourly = emptyList(),
        daily = emptyList(),
        airQuality = AirQuality(
            aqi = 25,
            category = AirQualityCategory.GOOD,
            pm25 = 10.0,
            pm10 = 20.0,
            co = 100.0,
            no2 = 10.0,
            so2 = 5.0,
            o3 = 25.0
        ),
        sunMoon = SunMoon(
            sunriseTime = "06:00 AM",
            sunsetTime = "06:00 PM",
            daylightDurationMinutes = 720
        ),
        lastUpdatedEpochSeconds = System.currentTimeMillis() / 1000
    )

    private class FakeWeatherProvider(
        var shouldThrow: Boolean = false,
        var reportToReturn: WeatherReport
    ) : WeatherProvider {
        override val id: String = "fake_provider"
        override val displayName: String = "Fake Provider"
        override val description: String = "Fake test provider"
        override val capabilities: ProviderCapabilities = ProviderCapabilities()

        override fun isConfigured(): Boolean = true

        override suspend fun fetchWeather(location: CityLocation): WeatherReport {
            if (shouldThrow) throw RuntimeException("Network connection failed")
            return reportToReturn
        }
    }

    private class FakeWeatherCacheDao : WeatherCacheDao {
        val cacheMap = mutableMapOf<String, WeatherCacheEntity>()

        override suspend fun getCachedWeather(locationId: String): WeatherCacheEntity? = cacheMap[locationId]
        override suspend fun insertCache(cache: WeatherCacheEntity) { cacheMap[cache.locationId] = cache }
        override suspend fun deleteCache(locationId: String) { cacheMap.remove(locationId) }
        override suspend fun pruneExpiredCache(nowEpochSeconds: Long) {
            cacheMap.entries.removeIf { it.value.expiresAtEpochSeconds < nowEpochSeconds }
        }
    }

    @Test
    fun testGetWeather_successfulFetch_returnsSuccessAndCaches() = runTest(testDispatcher) {
        val fakeProvider = FakeWeatherProvider(shouldThrow = false, reportToReturn = dummyReport)
        val fakeDao = FakeWeatherCacheDao()

        val repository = WeatherRepositoryImpl(
            providers = mapOf("fake_provider" to fakeProvider),
            weatherCacheDao = fakeDao,
            dispatchers = testDispatchers,
            activeProviderId = "fake_provider"
        )

        val result = repository.getWeather(dummyLocation, forceRefresh = true)
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(22.0, data.current.temperatureCelsius, 0.01)
        assertEquals("Test City", data.location.name)
        assertTrue(fakeDao.cacheMap.containsKey(dummyLocation.id))
    }

    @Test
    fun testGetWeather_networkFailureWithCache_returnsCachedData() = runTest(testDispatcher) {
        val fakeProvider = FakeWeatherProvider(shouldThrow = false, reportToReturn = dummyReport)
        val fakeDao = FakeWeatherCacheDao()

        val repository = WeatherRepositoryImpl(
            providers = mapOf("fake_provider" to fakeProvider),
            weatherCacheDao = fakeDao,
            dispatchers = testDispatchers,
            activeProviderId = "fake_provider"
        )

        // 1. First fetch successfully
        repository.getWeather(dummyLocation, forceRefresh = true)

        // 2. Network fails on refresh
        fakeProvider.shouldThrow = true

        val fallbackResult = repository.getWeather(dummyLocation, forceRefresh = true)
        assertTrue(fallbackResult is AppResult.Success)
        val fallbackData = (fallbackResult as AppResult.Success).data
        assertTrue(fallbackData.isCached)
    }

    @Test
    fun testGetWeather_networkFailureWithoutCache_returnsError() = runTest(testDispatcher) {
        val fakeProvider = FakeWeatherProvider(shouldThrow = true, reportToReturn = dummyReport)
        val fakeDao = FakeWeatherCacheDao()

        val repository = WeatherRepositoryImpl(
            providers = mapOf("fake_provider" to fakeProvider),
            weatherCacheDao = fakeDao,
            dispatchers = testDispatchers,
            activeProviderId = "fake_provider"
        )

        val result = repository.getWeather(dummyLocation, forceRefresh = true)
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals("Network connection failed", error.message)
    }
}
