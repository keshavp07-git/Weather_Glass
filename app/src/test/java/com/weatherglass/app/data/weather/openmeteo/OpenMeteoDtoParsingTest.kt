package com.weatherglass.app.data.weather.openmeteo

import com.weatherglass.app.core.network.NetworkClient
import com.weatherglass.app.data.weather.openmeteo.dto.OpenMeteoAirQualityResponse
import com.weatherglass.app.data.weather.openmeteo.dto.OpenMeteoForecastResponse
import com.weatherglass.app.data.weather.openmeteo.dto.OpenMeteoGeocodingResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OpenMeteoDtoParsingTest {

    @Test
    fun testParseForecastJson() {
        val jsonString = """
        {
            "latitude": 26.85,
            "longitude": 80.95,
            "timezone": "Asia/Kolkata",
            "current": {
                "time": "2026-11-09T18:00",
                "temperature_2m": 28.4,
                "relative_humidity_2m": 62,
                "apparent_temperature": 31.0,
                "is_day": 0,
                "precipitation": 0.0,
                "weather_code": 1,
                "cloud_cover": 25,
                "pressure_msl": 1014.2,
                "wind_speed_10m": 12.5,
                "wind_direction_10m": 290,
                "wind_gusts_10m": 18.2,
                "uv_index": 0.0,
                "dew_point_2m": 19.5
            },
            "hourly": {
                "time": ["2026-11-09T18:00", "2026-11-09T19:00"],
                "temperature_2m": [28.4, 27.1],
                "relative_humidity_2m": [62, 65],
                "precipitation_probability": [0, 5],
                "precipitation": [0.0, 0.0],
                "weather_code": [1, 1],
                "wind_speed_10m": [12.5, 10.8],
                "wind_direction_10m": [290, 285]
            },
            "daily": {
                "time": ["2026-11-09", "2026-11-10"],
                "weather_code": [1, 2],
                "temperature_2m_max": [30.2, 31.0],
                "temperature_2m_min": [19.0, 18.5],
                "sunrise": ["2026-11-09T06:15", "2026-11-10T06:16"],
                "sunset": ["2026-11-09T17:22", "2026-11-10T17:21"],
                "precipitation_sum": [0.0, 0.0],
                "precipitation_probability_max": [10, 5],
                "uv_index_max": [6.2, 6.0]
            }
        }
        """.trimIndent()

        val parsed = NetworkClient.json.decodeFromString<OpenMeteoForecastResponse>(jsonString)
        assertNotNull(parsed.current)
        assertEquals(28.4, parsed.current!!.temperature2m, 0.01)
        assertEquals(31.0, parsed.current!!.apparentTemperature, 0.01)
        assertEquals("Asia/Kolkata", parsed.timezone)
        assertEquals(2, parsed.hourly?.time?.size)
        assertEquals(2, parsed.daily?.time?.size)
    }

    @Test
    fun testParseAirQualityJson() {
        val jsonString = """
        {
            "latitude": 26.85,
            "longitude": 80.95,
            "timezone": "Asia/Kolkata",
            "current": {
                "time": "2026-11-09T18:00",
                "european_aqi": 42,
                "us_aqi": 115,
                "pm10": 78.4,
                "pm2_5": 41.2,
                "carbon_monoxide": 650.0,
                "nitrogen_dioxide": 28.5,
                "sulphur_dioxide": 12.1,
                "ozone": 34.0
            }
        }
        """.trimIndent()

        val parsed = NetworkClient.json.decodeFromString<OpenMeteoAirQualityResponse>(jsonString)
        assertNotNull(parsed.current)
        assertEquals(42, parsed.current!!.europeanAqi)
        assertEquals(41.2, parsed.current!!.pm25!!, 0.01)
    }

    @Test
    fun testParseGeocodingJson() {
        val jsonString = """
        {
            "results": [
                {
                    "id": 1264733,
                    "name": "Lucknow",
                    "latitude": 26.8467,
                    "longitude": 80.9462,
                    "country_code": "IN",
                    "country": "India",
                    "admin1": "Uttar Pradesh",
                    "timezone": "Asia/Kolkata"
                }
            ]
        }
        """.trimIndent()

        val parsed = NetworkClient.json.decodeFromString<OpenMeteoGeocodingResponse>(jsonString)
        assertNotNull(parsed.results)
        assertEquals(1, parsed.results!!.size)
        assertEquals("Lucknow", parsed.results!![0].name)
        assertEquals("India", parsed.results!![0].country)
    }
}
