package com.weatherglass.app.data.weather

import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.common.DispatcherProvider
import com.weatherglass.app.core.database.dao.WeatherCacheDao
import com.weatherglass.app.core.database.entity.WeatherCacheEntity
import com.weatherglass.app.core.network.NetworkClient
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.WeatherReport
import com.weatherglass.app.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.util.concurrent.ConcurrentHashMap

class WeatherRepositoryImpl(
    private val providers: Map<String, WeatherProvider>,
    private val weatherCacheDao: WeatherCacheDao,
    private val dispatchers: DispatcherProvider,
    private var activeProviderId: String = "open_meteo"
) : WeatherRepository {

    private val inMemoryCache = ConcurrentHashMap<String, WeatherReport>()
    private val cacheDurationSeconds = 20 * 60L // 20 minutes freshness

    override fun getAvailableProviders(): List<WeatherProvider> = providers.values.toList()

    override fun getActiveProvider(): WeatherProvider {
        return providers[activeProviderId] ?: providers["open_meteo"] ?: providers.values.first()
    }

    override fun setActiveProvider(providerId: String) {
        if (providers.containsKey(providerId)) {
            activeProviderId = providerId
        }
    }

    override suspend fun getWeather(location: CityLocation, forceRefresh: Boolean): AppResult<WeatherReport> = withContext(dispatchers.io) {
        val nowEpoch = System.currentTimeMillis() / 1000

        // Check memory cache first if not forced
        if (!forceRefresh) {
            inMemoryCache[location.id]?.let { cached ->
                if (nowEpoch - cached.lastUpdatedEpochSeconds < cacheDurationSeconds) {
                    return@withContext AppResult.Success(cached)
                }
            }

            // Check database cache
            val dbCache = runCatching { weatherCacheDao.getCachedWeather(location.id) }.getOrNull()
            if (dbCache != null && nowEpoch < dbCache.expiresAtEpochSeconds) {
                val decoded = runCatching {
                    NetworkClient.json.decodeFromString<WeatherReport>(dbCache.serializedWeatherReportJson)
                }.getOrNull()
                if (decoded != null) {
                    val marked = decoded.copy(isCached = true)
                    inMemoryCache[location.id] = marked
                    return@withContext AppResult.Success(marked)
                }
            }
        }

        // Fetch fresh from active provider
        val provider = getActiveProvider()
        try {
            val freshReport = provider.fetchWeather(location)
            inMemoryCache[location.id] = freshReport

            // Save to database
            val jsonString = NetworkClient.json.encodeToString(freshReport)
            val cacheEntity = WeatherCacheEntity(
                locationId = location.id,
                serializedWeatherReportJson = jsonString,
                cachedAtEpochSeconds = nowEpoch,
                expiresAtEpochSeconds = nowEpoch + cacheDurationSeconds,
                providerId = provider.id
            )
            weatherCacheDao.insertCache(cacheEntity)

            AppResult.Success(freshReport)
        } catch (e: Exception) {
            // Network or parsing failed -> Fallback to cached data if present
            val fallback = getCachedWeather(location.id)
            if (fallback != null) {
                AppResult.Success(fallback.copy(isCached = true))
            } else {
                AppResult.Error(e, e.message ?: "Failed to retrieve weather data.")
            }
        }
    }

    override suspend fun getCachedWeather(locationId: String): WeatherReport? = withContext(dispatchers.io) {
        inMemoryCache[locationId]?.copy(isCached = true) ?: run {
            val dbEntity = weatherCacheDao.getCachedWeather(locationId) ?: return@withContext null
            val decoded = runCatching {
                NetworkClient.json.decodeFromString<WeatherReport>(dbEntity.serializedWeatherReportJson)
            }.getOrNull()
            decoded?.copy(isCached = true)
        }
    }
}
