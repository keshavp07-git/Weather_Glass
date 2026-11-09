package com.weatherglass.app.data.weather.weatherapi.api

import com.weatherglass.app.data.weather.weatherapi.dto.WeatherApiForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiRestApi {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes"
    ): WeatherApiForecastResponse
}
