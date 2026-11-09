package com.weatherglass.app.data.cities

import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.common.DispatcherProvider
import com.weatherglass.app.core.database.dao.FavoriteCityDao
import com.weatherglass.app.core.database.dao.RecentSearchDao
import com.weatherglass.app.core.database.entity.FavoriteCityEntity
import com.weatherglass.app.core.database.entity.RecentSearchEntity
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoGeocodingApi
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.repository.CitySearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class CitySearchRepositoryImpl(
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val favoriteCityDao: FavoriteCityDao,
    private val recentSearchDao: RecentSearchDao,
    private val dispatchers: DispatcherProvider
) : CitySearchRepository {

    private val geocodingCache = ConcurrentHashMap<String, List<CityLocation>>()

    // Default starter cities for initial launch
    val defaultStarterCities = listOf(
        CityLocation(
            id = "geo_lucknow_in",
            name = "Lucknow",
            region = "Uttar Pradesh",
            country = "India",
            latitude = 26.8467,
            longitude = 80.9462,
            timezone = "Asia/Kolkata",
            isFavorite = true
        ),
        CityLocation(
            id = "geo_london_gb",
            name = "London",
            region = "Greater London",
            country = "United Kingdom",
            latitude = 51.5074,
            longitude = -0.1278,
            timezone = "Europe/London",
            isFavorite = true
        ),
        CityLocation(
            id = "geo_tokyo_jp",
            name = "Tokyo",
            region = "Tokyo",
            country = "Japan",
            latitude = 35.6762,
            longitude = 139.6503,
            timezone = "Asia/Tokyo",
            isFavorite = true
        ),
        CityLocation(
            id = "geo_newyork_us",
            name = "New York",
            region = "New York",
            country = "United States",
            latitude = 40.7128,
            longitude = -74.0060,
            timezone = "America/New_York",
            isFavorite = true
        ),
        CityLocation(
            id = "geo_dubai_ae",
            name = "Dubai",
            region = "Dubai",
            country = "United Arab Emirates",
            latitude = 25.2048,
            longitude = 55.2708,
            timezone = "Asia/Dubai",
            isFavorite = true
        )
    )

    suspend fun ensureDefaultFavoritesIfEmpty() = withContext(dispatchers.io) {
        if (favoriteCityDao.getCount() == 0) {
            defaultStarterCities.forEachIndexed { index, city ->
                favoriteCityDao.insertFavorite(
                    FavoriteCityEntity(
                        id = city.id,
                        name = city.name,
                        region = city.region,
                        country = city.country,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        timezone = city.timezone,
                        displayOrder = index,
                        isCurrentLocation = city.isCurrentLocation
                    )
                )
            }
        }
    }

    override suspend fun searchCities(query: String): AppResult<List<CityLocation>> = withContext(dispatchers.io) {
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            return@withContext AppResult.Success(emptyList())
        }

        val cacheKey = trimmed.lowercase()
        geocodingCache[cacheKey]?.let {
            return@withContext AppResult.Success(it)
        }

        try {
            val response = geocodingApi.searchLocations(name = trimmed, count = 15)
            val results = response.results?.map { item ->
                val id = "geo_${item.id}_${item.countryCode ?: "xx"}"
                CityLocation(
                    id = id,
                    name = item.name,
                    region = item.admin1 ?: item.admin2 ?: "",
                    country = item.country ?: "",
                    latitude = item.latitude,
                    longitude = item.longitude,
                    timezone = item.timezone ?: "UTC",
                    isFavorite = favoriteCityDao.isFavorite(id)
                )
            } ?: emptyList()

            geocodingCache[cacheKey] = results
            AppResult.Success(results)
        } catch (e: Exception) {
            // Local fallback match if offline
            val localMatches = defaultStarterCities.filter {
                it.name.contains(trimmed, ignoreCase = true) ||
                it.country.contains(trimmed, ignoreCase = true) ||
                it.region.contains(trimmed, ignoreCase = true)
            }
            if (localMatches.isNotEmpty()) {
                AppResult.Success(localMatches)
            } else {
                AppResult.Error(e, "Search failed: ${e.localizedMessage ?: "Network error"}")
            }
        }
    }

    override fun getRecentSearches(): Flow<List<CityLocation>> {
        return recentSearchDao.getRecentSearches().map { entities ->
            entities.map { entity ->
                CityLocation(
                    id = entity.id,
                    name = entity.name,
                    region = entity.region,
                    country = entity.country,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    timezone = entity.timezone
                )
            }
        }
    }

    override suspend fun saveRecentSearch(city: CityLocation) = withContext(dispatchers.io) {
        recentSearchDao.insertRecentSearch(
            RecentSearchEntity(
                id = city.id,
                name = city.name,
                region = city.region,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone,
                searchedAtEpochSeconds = System.currentTimeMillis() / 1000
            )
        )
    }

    override suspend fun clearRecentSearches() = withContext(dispatchers.io) {
        recentSearchDao.clearAllRecentSearches()
    }

    override fun getFavoriteCities(): Flow<List<CityLocation>> {
        return favoriteCityDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                CityLocation(
                    id = entity.id,
                    name = entity.name,
                    region = entity.region,
                    country = entity.country,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    timezone = entity.timezone,
                    isCurrentLocation = entity.isCurrentLocation,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun addFavorite(city: CityLocation) = withContext(dispatchers.io) {
        val count = favoriteCityDao.getCount()
        favoriteCityDao.insertFavorite(
            FavoriteCityEntity(
                id = city.id,
                name = city.name,
                region = city.region,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone,
                displayOrder = count,
                isCurrentLocation = city.isCurrentLocation
            )
        )
    }

    override suspend fun removeFavorite(cityId: String) = withContext(dispatchers.io) {
        favoriteCityDao.deleteFavoriteById(cityId)
    }

    override suspend fun updateFavoriteOrder(cities: List<CityLocation>) = withContext(dispatchers.io) {
        val entities = cities.mapIndexed { index, city ->
            FavoriteCityEntity(
                id = city.id,
                name = city.name,
                region = city.region,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone,
                displayOrder = index,
                isCurrentLocation = city.isCurrentLocation
            )
        }
        favoriteCityDao.updateFavorites(entities)
    }

    override suspend fun isFavorite(cityId: String): Boolean = withContext(dispatchers.io) {
        favoriteCityDao.isFavorite(cityId)
    }
}
