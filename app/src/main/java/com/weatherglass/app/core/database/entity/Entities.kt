package com.weatherglass.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val displayOrder: Int,
    val isCurrentLocation: Boolean = false,
    val addedAtEpochSeconds: Long = System.currentTimeMillis() / 1000
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val searchedAtEpochSeconds: Long = System.currentTimeMillis() / 1000
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val locationId: String,
    val serializedWeatherReportJson: String,
    val cachedAtEpochSeconds: Long,
    val expiresAtEpochSeconds: Long,
    val providerId: String
)
