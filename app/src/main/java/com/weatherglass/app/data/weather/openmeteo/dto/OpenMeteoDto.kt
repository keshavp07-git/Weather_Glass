package com.weatherglass.app.data.weather.openmeteo.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val current: OpenMeteoCurrent? = null,
    val hourly: OpenMeteoHourly? = null,
    val daily: OpenMeteoDaily? = null
)

@Serializable
data class OpenMeteoCurrent(
    val time: String? = null,
    val interval: Int? = null,
    @SerialName("temperature_2m") val temperature2m: Double = 0.0,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int = 0,
    @SerialName("apparent_temperature") val apparentTemperature: Double = 0.0,
    @SerialName("is_day") val isDay: Int = 1,
    val precipitation: Double = 0.0,
    @SerialName("weather_code") val weatherCode: Int = 0,
    @SerialName("cloud_cover") val cloudCover: Int = 0,
    @SerialName("pressure_msl") val pressureMsl: Double = 1013.25,
    @SerialName("surface_pressure") val surfacePressure: Double = 1013.25,
    @SerialName("wind_speed_10m") val windSpeed10m: Double = 0.0,
    @SerialName("wind_direction_10m") val windDirection10m: Int = 0,
    @SerialName("wind_gusts_10m") val windGusts10m: Double = 0.0,
    @SerialName("uv_index") val uvIndex: Double = 0.0,
    @SerialName("dew_point_2m") val dewPoint2m: Double = 0.0
)

@Serializable
data class OpenMeteoHourly(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature2m: List<Double> = emptyList(),
    @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Int> = emptyList(),
    @SerialName("precipitation_probability") val precipitationProbability: List<Int> = emptyList(),
    val precipitation: List<Double> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerialName("wind_speed_10m") val windSpeed10m: List<Double> = emptyList(),
    @SerialName("wind_direction_10m") val windDirection10m: List<Int> = emptyList()
)

@Serializable
data class OpenMeteoDaily(
    val time: List<String> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerialName("temperature_2m_max") val temperature2mMax: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val temperature2mMin: List<Double> = emptyList(),
    val sunrise: List<String> = emptyList(),
    val sunset: List<String> = emptyList(),
    @SerialName("precipitation_sum") val precipitationSum: List<Double> = emptyList(),
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList(),
    @SerialName("uv_index_max") val uvIndexMax: List<Double> = emptyList()
)

@Serializable
data class OpenMeteoAirQualityResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val current: OpenMeteoAirQualityCurrent? = null
)

@Serializable
data class OpenMeteoAirQualityCurrent(
    val time: String? = null,
    @SerialName("european_aqi") val europeanAqi: Int? = null,
    @SerialName("us_aqi") val usAqi: Int? = null,
    @SerialName("pm10") val pm10: Double? = null,
    @SerialName("pm2_5") val pm25: Double? = null,
    @SerialName("carbon_monoxide") val carbonMonoxide: Double? = null,
    @SerialName("nitrogen_dioxide") val nitrogenDioxide: Double? = null,
    @SerialName("sulphur_dioxide") val sulphurDioxide: Double? = null,
    val ozone: Double? = null
)

@Serializable
data class OpenMeteoGeocodingResponse(
    val results: List<OpenMeteoGeocodingItem>? = null,
    @SerialName("generationtime_ms") val generationTimeMs: Double? = null
)

@Serializable
data class OpenMeteoGeocodingItem(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    @SerialName("feature_code") val featureCode: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null,
    val admin2: String? = null,
    val timezone: String? = null,
    val population: Long? = null
)
