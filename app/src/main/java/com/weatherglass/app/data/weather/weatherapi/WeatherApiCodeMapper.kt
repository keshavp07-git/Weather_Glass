package com.weatherglass.app.data.weather.weatherapi

import com.weatherglass.app.domain.model.WeatherCondition

object WeatherApiCodeMapper {

    fun mapWeatherCode(code: Int, isDay: Boolean): Pair<WeatherCondition, String> {
        return when (code) {
            1000 -> if (isDay) (WeatherCondition.CLEAR_DAY to "Clear Sky") else (WeatherCondition.CLEAR_NIGHT to "Clear Night")
            1003 -> if (isDay) (WeatherCondition.PARTLY_CLOUDY_DAY to "Partly Cloudy") else (WeatherCondition.PARTLY_CLOUDY_NIGHT to "Partly Cloudy")
            1006 -> (WeatherCondition.OVERCAST to "Cloudy")
            1009 -> (WeatherCondition.OVERCAST to "Overcast")
            1030, 1036, 1135, 1147 -> (WeatherCondition.FOG to "Fog / Haze")
            1063, 1150, 1153, 1180, 1183 -> (WeatherCondition.LIGHT_DRIZZLE to "Light Drizzle")
            1186, 1189, 1192, 1195, 1240, 1243, 1246 -> (WeatherCondition.RAIN to "Rain")
            1087, 1273, 1276, 1279, 1282 -> (WeatherCondition.THUNDERSTORM to "Thunderstorm")
            1066, 1114, 1210, 1213, 1216, 1219, 1255 -> (WeatherCondition.SNOW to "Snow")
            1117, 1222, 1225, 1258 -> (WeatherCondition.HEAVY_SNOW to "Heavy Snow")
            else -> if (isDay) (WeatherCondition.PARTLY_CLOUDY_DAY to "Partly Cloudy") else (WeatherCondition.PARTLY_CLOUDY_NIGHT to "Partly Cloudy")
        }
    }
}
