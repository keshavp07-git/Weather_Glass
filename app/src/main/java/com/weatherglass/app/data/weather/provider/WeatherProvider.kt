package com.weatherglass.app.data.weather.provider

import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.WeatherReport

data class ProviderCapabilities(
    val supportsCurrent: Boolean = true,
    val supportsHourly: Boolean = true,
    val supportsDaily: Boolean = true,
    val supportsAirQuality: Boolean = false,
    val supportsAlerts: Boolean = false,
    val requiresApiKey: Boolean = false,
    val maxDailyDays: Int = 7
)

data class ProviderStatus(
    val id: String,
    val displayName: String,
    val description: String,
    val isConfigured: Boolean,
    val capabilities: ProviderCapabilities,
    val lastSyncEpochSeconds: Long? = null,
    val lastError: String? = null
)

interface WeatherProvider {
    val id: String
    val displayName: String
    val description: String
    val capabilities: ProviderCapabilities

    fun isConfigured(): Boolean
    suspend fun fetchWeather(location: CityLocation): WeatherReport
}
