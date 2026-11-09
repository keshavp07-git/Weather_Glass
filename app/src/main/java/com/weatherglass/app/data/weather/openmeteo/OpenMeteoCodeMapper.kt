package com.weatherglass.app.data.weather.openmeteo

import com.weatherglass.app.domain.model.WeatherCondition

object OpenMeteoCodeMapper {

    fun mapWeatherCode(code: Int, isDay: Boolean): Pair<WeatherCondition, String> {
        return when (code) {
            0 -> if (isDay) (WeatherCondition.CLEAR_DAY to "Clear Sky") else (WeatherCondition.CLEAR_NIGHT to "Clear Night")
            1, 2 -> if (isDay) (WeatherCondition.PARTLY_CLOUDY_DAY to "Partly Cloudy") else (WeatherCondition.PARTLY_CLOUDY_NIGHT to "Partly Cloudy")
            3 -> (WeatherCondition.OVERCAST to "Overcast")
            45, 48 -> (WeatherCondition.FOG to "Foggy")
            51, 53, 55 -> (WeatherCondition.LIGHT_DRIZZLE to "Drizzle")
            56, 57 -> (WeatherCondition.LIGHT_DRIZZLE to "Freezing Drizzle")
            61, 63 -> (WeatherCondition.RAIN to "Rain")
            65 -> (WeatherCondition.HEAVY_RAIN to "Heavy Rain")
            66, 67 -> (WeatherCondition.RAIN to "Freezing Rain")
            71, 73 -> (WeatherCondition.SNOW to "Snow")
            75, 77 -> (WeatherCondition.HEAVY_SNOW to "Heavy Snow")
            80, 81 -> (WeatherCondition.RAIN to "Rain Showers")
            82 -> (WeatherCondition.HEAVY_RAIN to "Heavy Showers")
            85, 86 -> (WeatherCondition.SNOW to "Snow Showers")
            95 -> (WeatherCondition.THUNDERSTORM to "Thunderstorm")
            96, 99 -> (WeatherCondition.THUNDERSTORM to "Severe Thunderstorm with Hail")
            else -> if (isDay) (WeatherCondition.PARTLY_CLOUDY_DAY to "Partly Cloudy") else (WeatherCondition.PARTLY_CLOUDY_NIGHT to "Partly Cloudy")
        }
    }
}
