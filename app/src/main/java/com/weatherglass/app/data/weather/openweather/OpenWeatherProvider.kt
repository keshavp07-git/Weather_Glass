package com.weatherglass.app.data.weather.openweather

import com.weatherglass.app.data.weather.provider.ProviderCapabilities
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.WeatherReport

class OpenWeatherProvider(
    private var apiKey: String = ""
) : WeatherProvider {

    override val id: String = "open_weather"
    override val displayName: String = "OpenWeatherMap"
    override val description: String = "OpenWeather OneCall 3.0 API. Requires an active subscription key."
    override val capabilities: ProviderCapabilities = ProviderCapabilities(
        supportsCurrent = true,
        supportsHourly = true,
        supportsDaily = true,
        supportsAirQuality = false,
        supportsAlerts = true,
        requiresApiKey = true,
        maxDailyDays = 8
    )

    fun setApiKey(key: String) {
        apiKey = key
    }

    override fun isConfigured(): Boolean = apiKey.isNotBlank()

    override suspend fun fetchWeather(location: CityLocation): WeatherReport {
        if (!isConfigured()) {
            throw IllegalStateException("OpenWeather API key is not configured. Please provide a key in Developer Settings.")
        }
        throw UnsupportedOperationException("OpenWeather key provided is not authenticated with the remote service.")
    }
}
