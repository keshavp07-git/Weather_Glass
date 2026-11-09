package com.weatherglass.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.weatherglass.app.core.database.entity.FavoriteCityEntity
import com.weatherglass.app.core.database.entity.RecentSearchEntity
import com.weatherglass.app.core.database.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY displayOrder ASC")
    fun getAllFavorites(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT * FROM favorite_cities WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: String): FavoriteCityEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :id)")
    fun isFavoriteFlow(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteCityEntity)

    @Update
    suspend fun updateFavorites(favorites: List<FavoriteCityEntity>)

    @Query("DELETE FROM favorite_cities WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("SELECT COUNT(*) FROM favorite_cities")
    suspend fun getCount(): Int
}

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY searchedAtEpochSeconds DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE id = :id")
    suspend fun deleteRecentSearch(id: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAllRecentSearches()
}

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE locationId = :locationId LIMIT 1")
    suspend fun getCachedWeather(locationId: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE locationId = :locationId")
    suspend fun deleteCache(locationId: String)

    @Query("DELETE FROM weather_cache WHERE expiresAtEpochSeconds < :nowEpochSeconds")
    suspend fun pruneExpiredCache(nowEpochSeconds: Long)
}
