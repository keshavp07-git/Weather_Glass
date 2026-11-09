package com.weatherglass.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class WeatherCondition(val label: String, val isDay: Boolean = true) {
    CLEAR_DAY("Clear Sky", true),
    CLEAR_NIGHT("Clear Night", false),
    PARTLY_CLOUDY_DAY("Partly Cloudy", true),
    PARTLY_CLOUDY_NIGHT("Partly Cloudy", false),
    OVERCAST("Overcast", true),
    FOG("Foggy", true),
    LIGHT_DRIZZLE("Light Drizzle", true),
    RAIN("Rain", true),
    HEAVY_RAIN("Heavy Rain", true),
    THUNDERSTORM("Thunderstorm", true),
    SNOW("Snow", true),
    HEAVY_SNOW("Heavy Snow", true),
    WINDY("Windy", true);

    val isPrecipitating: Boolean
        get() = this in listOf(LIGHT_DRIZZLE, RAIN, HEAVY_RAIN, THUNDERSTORM, SNOW, HEAVY_SNOW)
}

@Serializable
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class CityLocation(
    val id: String,
    val name: String,
    val region: String = "",
    val country: String = "",
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "UTC",
    val isCurrentLocation: Boolean = false,
    val isFavorite: Boolean = false
) {
    val displaySubtitle: String
        get() = listOf(region, country).filter { it.isNotBlank() }.joinToString(", ")
}

@Serializable
data class CurrentWeather(
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val condition: WeatherCondition,
    val conditionDescription: String,
    val highTempCelsius: Double,
    val lowTempCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Int,
    val windGustsKmh: Double,
    val pressureHpa: Double,
    val visibilityKm: Double,
    val cloudCoverPercent: Int,
    val dewPointCelsius: Double,
    val uvIndex: Double,
    val precipitationMm: Double,
    val rainProbabilityPercent: Int,
    val isDay: Boolean = true
)

@Serializable
data class HourlyForecast(
    val epochSeconds: Long,
    val formattedTime: String,
    val temperatureCelsius: Double,
    val condition: WeatherCondition,
    val precipitationProbabilityPercent: Int,
    val precipitationMm: Double,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Int,
    val humidityPercent: Int,
    val isNow: Boolean = false
)

@Serializable
data class DailyForecast(
    val epochSeconds: Long,
    val dateIso: String,
    val dayName: String,
    val condition: WeatherCondition,
    val conditionDescription: String,
    val maxTempCelsius: Double,
    val minTempCelsius: Double,
    val precipitationProbabilityPercent: Int,
    val precipitationMm: Double,
    val snowfallCm: Double = 0.0,
    val uvIndexMax: Double = 0.0,
    val sunrise: String = "",
    val sunset: String = ""
)

@Serializable
enum class AirQualityCategory(val label: String, val hexColor: Long, val healthAdvice: String) {
    GOOD("Good", 0xFF4CAF50, "Air quality is satisfactory and poses little or no risk."),
    FAIR("Fair", 0xFF8BC34A, "Air quality is acceptable for most individuals."),
    MODERATE("Moderate", 0xFFFFC107, "Sensitive groups may experience minor discomfort."),
    POOR("Poor", 0xFFFF9800, "Wear a mask if sensitive. Limit prolonged outdoor exertion."),
    VERY_POOR("Very Poor", 0xFFF44336, "Health warnings of emergency conditions. Avoid outdoor activities."),
    EXTREMELY_POOR("Hazardous", 0xFF9C27B0, "Everyone may experience serious health effects. Stay indoors.");

    companion object {
        fun fromEuropeanAqi(aqi: Int): AirQualityCategory = when (aqi) {
            in 0..20 -> GOOD
            in 21..40 -> FAIR
            in 41..60 -> MODERATE
            in 61..80 -> POOR
            in 81..100 -> VERY_POOR
            else -> EXTREMELY_POOR
        }

        fun fromUsAqi(aqi: Int): AirQualityCategory = when (aqi) {
            in 0..50 -> GOOD
            in 51..100 -> FAIR
            in 101..150 -> MODERATE
            in 151..200 -> POOR
            in 201..300 -> VERY_POOR
            else -> EXTREMELY_POOR
        }
    }
}

@Serializable
data class AirQuality(
    val aqi: Int,
    val category: AirQualityCategory,
    val pm25: Double,
    val pm10: Double,
    val co: Double,
    val no2: Double,
    val so2: Double,
    val o3: Double,
    val isAvailable: Boolean = true
)

@Serializable
data class SunMoon(
    val sunriseTime: String,
    val sunsetTime: String,
    val daylightDurationMinutes: Int,
    val moonriseTime: String = "",
    val moonsetTime: String = "",
    val moonPhaseName: String = "Waxing Gibbous",
    val moonPhaseFraction: Float = 0.65f,
    val moonIlluminationPercent: Int = 78
)

@Serializable
data class WeatherAlert(
    val id: String,
    val title: String,
    val severity: String,
    val description: String,
    val issuingAuthority: String,
    val effectiveTime: String,
    val expiryTime: String,
    val precautions: String = ""
)

@Serializable
data class WeatherReport(
    val location: CityLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val airQuality: AirQuality?,
    val sunMoon: SunMoon,
    val alerts: List<WeatherAlert> = emptyList(),
    val lastUpdatedEpochSeconds: Long,
    val isCached: Boolean = false,
    val providerId: String = "open_meteo",
    val providerName: String = "Open-Meteo"
)
