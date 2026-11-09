package com.weatherglass.app.data.weather.weatherapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiForecastResponse(
    val location: WeatherApiLocation? = null,
    val current: WeatherApiCurrent? = null,
    val forecast: WeatherApiForecastContainer? = null,
    val alerts: WeatherApiAlertsContainer? = null
)

@Serializable
data class WeatherApiLocation(
    val name: String = "",
    val region: String = "",
    val country: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    @SerialName("tz_id") val tzId: String = "UTC",
    @SerialName("localtime_epoch") val localtimeEpoch: Long = 0L
)

@Serializable
data class WeatherApiCondition(
    val text: String = "",
    val icon: String = "",
    val code: Int = 1000
)

@Serializable
data class WeatherApiAirQuality(
    val co: Double = 0.0,
    val no2: Double = 0.0,
    val o3: Double = 0.0,
    val so2: Double = 0.0,
    @SerialName("pm2_5") val pm25: Double = 0.0,
    val pm10: Double = 0.0,
    @SerialName("us-epa-index") val usEpaIndex: Int = 1,
    @SerialName("gb-defra-index") val gbDefraIndex: Int = 1
)

@Serializable
data class WeatherApiCurrent(
    @SerialName("last_updated_epoch") val lastUpdatedEpoch: Long = 0L,
    @SerialName("temp_c") val tempC: Double = 0.0,
    @SerialName("is_day") val isDay: Int = 1,
    val condition: WeatherApiCondition? = null,
    @SerialName("wind_kph") val windKph: Double = 0.0,
    @SerialName("wind_degree") val windDegree: Int = 0,
    @SerialName("pressure_mb") val pressureMb: Double = 1013.25,
    @SerialName("precip_mm") val precipMm: Double = 0.0,
    val humidity: Int = 0,
    val cloud: Int = 0,
    @SerialName("feelslike_c") val feelslikeC: Double = 0.0,
    @SerialName("vis_km") val visKm: Double = 10.0,
    val uv: Double = 0.0,
    @SerialName("gust_kph") val gustKph: Double = 0.0,
    @SerialName("dewpoint_c") val dewpointC: Double = 0.0,
    @SerialName("air_quality") val airQuality: WeatherApiAirQuality? = null
)

@Serializable
data class WeatherApiForecastContainer(
    val forecastday: List<WeatherApiForecastDay> = emptyList()
)

@Serializable
data class WeatherApiForecastDay(
    val date: String = "",
    @SerialName("date_epoch") val dateEpoch: Long = 0L,
    val day: WeatherApiDayData? = null,
    val astro: WeatherApiAstro? = null,
    val hour: List<WeatherApiHour> = emptyList()
)

@Serializable
data class WeatherApiDayData(
    @SerialName("maxtemp_c") val maxtempC: Double = 0.0,
    @SerialName("mintemp_c") val mintempC: Double = 0.0,
    @SerialName("avgtemp_c") val avgtempC: Double = 0.0,
    @SerialName("totalprecip_mm") val totalprecipMm: Double = 0.0,
    @SerialName("totalsnow_cm") val totalsnowCm: Double = 0.0,
    @SerialName("daily_chance_of_rain") val dailyChanceOfRain: Int = 0,
    val condition: WeatherApiCondition? = null,
    val uv: Double = 0.0
)

@Serializable
data class WeatherApiAstro(
    val sunrise: String = "06:00 AM",
    val sunset: String = "06:00 PM",
    val moonrise: String = "08:00 PM",
    val moonset: String = "07:00 AM",
    @SerialName("moon_phase") val moonPhase: String = "Waxing Gibbous",
    @SerialName("moon_illumination") val moonIllumination: Int = 50
)

@Serializable
data class WeatherApiHour(
    @SerialName("time_epoch") val timeEpoch: Long = 0L,
    val time: String = "",
    @SerialName("temp_c") val tempC: Double = 0.0,
    @SerialName("is_day") val isDay: Int = 1,
    val condition: WeatherApiCondition? = null,
    @SerialName("wind_kph") val windKph: Double = 0.0,
    @SerialName("wind_degree") val windDegree: Int = 0,
    val humidity: Int = 0,
    @SerialName("chance_of_rain") val chanceOfRain: Int = 0,
    @SerialName("precip_mm") val precipMm: Double = 0.0
)

@Serializable
data class WeatherApiAlertsContainer(
    val alert: List<WeatherApiAlertItem> = emptyList()
)

@Serializable
data class WeatherApiAlertItem(
    val headline: String = "",
    val severity: String = "",
    val event: String = "",
    val desc: String = "",
    val instruction: String = ""
)
