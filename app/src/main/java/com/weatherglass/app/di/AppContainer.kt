package com.weatherglass.app.di

import android.content.Context
import com.weatherglass.app.core.common.DefaultDispatcherProvider
import com.weatherglass.app.core.common.DispatcherProvider
import com.weatherglass.app.core.database.WeatherDatabase
import com.weatherglass.app.core.location.LocationProvider
import com.weatherglass.app.core.network.NetworkClient
import com.weatherglass.app.core.preferences.UserPreferencesRepository
import com.weatherglass.app.data.cities.CitySearchRepositoryImpl
import com.weatherglass.app.data.weather.WeatherRepositoryImpl
import com.weatherglass.app.data.weather.openmeteo.OpenMeteoProvider
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoAirQualityApi
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoForecastApi
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoGeocodingApi
import com.weatherglass.app.data.weather.openweather.OpenWeatherProvider
import com.weatherglass.app.data.weather.weatherapi.WeatherApiProvider
import com.weatherglass.app.domain.repository.CitySearchRepository
import com.weatherglass.app.domain.repository.WeatherRepository

interface AppContainer {
    val context: Context
    val dispatchers: DispatcherProvider
    val database: WeatherDatabase
    val userPreferencesRepository: UserPreferencesRepository
    val locationProvider: LocationProvider
    val weatherRepository: WeatherRepository
    val citySearchRepository: CitySearchRepository
    val openMeteoProvider: OpenMeteoProvider
    val weatherApiProvider: WeatherApiProvider
    val openWeatherProvider: OpenWeatherProvider
}

class DefaultAppContainer(override val context: Context) : AppContainer {

    override val dispatchers: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    override val database: WeatherDatabase by lazy {
        WeatherDatabase.getInstance(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val locationProvider: LocationProvider by lazy {
        LocationProvider(context, dispatchers)
    }

    private val forecastRetrofit by lazy {
        NetworkClient.createRetrofit("https://api.open-meteo.com/")
    }

    private val airQualityRetrofit by lazy {
        NetworkClient.createRetrofit("https://air-quality-api.open-meteo.com/")
    }

    private val geocodingRetrofit by lazy {
        NetworkClient.createRetrofit("https://geocoding-api.open-meteo.com/")
    }

    private val forecastApi: OpenMeteoForecastApi by lazy {
        forecastRetrofit.create(OpenMeteoForecastApi::class.java)
    }

    private val airQualityApi: OpenMeteoAirQualityApi by lazy {
        airQualityRetrofit.create(OpenMeteoAirQualityApi::class.java)
    }

    private val geocodingApi: OpenMeteoGeocodingApi by lazy {
        geocodingRetrofit.create(OpenMeteoGeocodingApi::class.java)
    }

    override val openMeteoProvider: OpenMeteoProvider by lazy {
        OpenMeteoProvider(forecastApi, airQualityApi)
    }

    private val weatherApiRetrofit by lazy {
        NetworkClient.createRetrofit("https://api.weatherapi.com/")
    }

    private val weatherApiRestApi: com.weatherglass.app.data.weather.weatherapi.api.WeatherApiRestApi by lazy {
        weatherApiRetrofit.create(com.weatherglass.app.data.weather.weatherapi.api.WeatherApiRestApi::class.java)
    }

    override val weatherApiProvider: WeatherApiProvider by lazy {
        WeatherApiProvider(
            api = weatherApiRestApi,
            apiKey = "ce4c103b27064f1bb41133356260911"
        )
    }

    override val openWeatherProvider: OpenWeatherProvider by lazy {
        OpenWeatherProvider()
    }

    private val providersMap by lazy {
        mapOf(
            openMeteoProvider.id to openMeteoProvider,
            weatherApiProvider.id to weatherApiProvider,
            openWeatherProvider.id to openWeatherProvider
        )
    }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(
            providers = providersMap,
            weatherCacheDao = database.weatherCacheDao(),
            dispatchers = dispatchers
        )
    }

    override val citySearchRepository: CitySearchRepository by lazy {
        CitySearchRepositoryImpl(
            geocodingApi = geocodingApi,
            favoriteCityDao = database.favoriteCityDao(),
            recentSearchDao = database.recentSearchDao(),
            dispatchers = dispatchers
        )
    }
}
