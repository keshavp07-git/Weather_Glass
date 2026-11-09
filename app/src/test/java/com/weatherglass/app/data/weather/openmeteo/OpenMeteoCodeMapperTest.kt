package com.weatherglass.app.data.weather.openmeteo

import com.weatherglass.app.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenMeteoCodeMapperTest {

    @Test
    fun testClearDayAndNightMapping() {
        val (dayCond, dayLabel) = OpenMeteoCodeMapper.mapWeatherCode(0, isDay = true)
        assertEquals(WeatherCondition.CLEAR_DAY, dayCond)
        assertEquals("Clear Sky", dayLabel)

        val (nightCond, nightLabel) = OpenMeteoCodeMapper.mapWeatherCode(0, isDay = false)
        assertEquals(WeatherCondition.CLEAR_NIGHT, nightCond)
        assertEquals("Clear Night", nightLabel)
    }

    @Test
    fun testRainAndThunderstormMapping() {
        val (rainCond, _) = OpenMeteoCodeMapper.mapWeatherCode(61, isDay = true)
        assertEquals(WeatherCondition.RAIN, rainCond)
        assertTrue(rainCond.isPrecipitating)

        val (thunderCond, _) = OpenMeteoCodeMapper.mapWeatherCode(95, isDay = true)
        assertEquals(WeatherCondition.THUNDERSTORM, thunderCond)
        assertTrue(thunderCond.isPrecipitating)

        val (snowCond, _) = OpenMeteoCodeMapper.mapWeatherCode(71, isDay = true)
        assertEquals(WeatherCondition.SNOW, snowCond)
        assertTrue(snowCond.isPrecipitating)
    }
}
