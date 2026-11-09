package com.weatherglass.app.domain.repository

import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.WeatherReport

interface WeatherRepository {
    suspend fun getWeather(location: CityLocation, forceRefresh: Boolean = false): AppResult<WeatherReport>
    suspend fun getCachedWeather(locationId: String): WeatherReport?
    fun getAvailableProviders(): List<WeatherProvider>
    fun getActiveProvider(): WeatherProvider
    fun setActiveProvider(providerId: String)
}
