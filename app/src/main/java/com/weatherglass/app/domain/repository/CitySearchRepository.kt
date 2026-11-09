package com.weatherglass.app.domain.repository

import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.domain.model.CityLocation
import kotlinx.coroutines.flow.Flow

interface CitySearchRepository {
    suspend fun searchCities(query: String): AppResult<List<CityLocation>>
    fun getRecentSearches(): Flow<List<CityLocation>>
    suspend fun saveRecentSearch(city: CityLocation)
    suspend fun clearRecentSearches()
    fun getFavoriteCities(): Flow<List<CityLocation>>
    suspend fun addFavorite(city: CityLocation)
    suspend fun removeFavorite(cityId: String)
    suspend fun updateFavoriteOrder(cities: List<CityLocation>)
    suspend fun isFavorite(cityId: String): Boolean
}
