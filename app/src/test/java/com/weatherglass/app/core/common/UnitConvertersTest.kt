package com.weatherglass.app.core.common

import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConvertersTest {

    @Test
    fun testTemperatureConversion_celsiusToFahrenheit() {
        // 0°C -> 32°F
        assertEquals("32°", UnitConverters.formatTemperature(0.0, TemperatureUnit.FAHRENHEIT))
        // 100°C -> 212°F
        assertEquals("212°", UnitConverters.formatTemperature(100.0, TemperatureUnit.FAHRENHEIT))
        // 25°C -> 77°F
        assertEquals("77°", UnitConverters.formatTemperature(25.0, TemperatureUnit.FAHRENHEIT))
        // -10°C -> 14°F
        assertEquals("14°", UnitConverters.formatTemperature(-10.0, TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun testTemperatureConversion_celsiusIdentity() {
        assertEquals("28°", UnitConverters.formatTemperature(28.2, TemperatureUnit.CELSIUS))
        assertEquals("-5°", UnitConverters.formatTemperature(-4.8, TemperatureUnit.CELSIUS))
    }

    @Test
    fun testWindSpeedConversion() {
        // 36 km/h -> 10 m/s
        assertEquals("10.0 m/s", UnitConverters.formatWindSpeed(36.0, WindSpeedUnit.MS))
        // 100 km/h -> 62.1 mph
        assertEquals("62.1 mph", UnitConverters.formatWindSpeed(100.0, WindSpeedUnit.MPH))
        // 100 km/h -> 54.0 kn
        assertEquals("54.0 kn", UnitConverters.formatWindSpeed(100.0, WindSpeedUnit.KNOTS))
    }

    @Test
    fun testPressureConversion() {
        // 1013.25 hPa -> 1013 hPa
        assertEquals("1013 hPa", UnitConverters.formatPressure(1013.25, PressureUnit.HPA))
        // 1013.25 hPa -> 760 mmHg
        assertEquals("760 mmHg", UnitConverters.formatPressure(1013.25, PressureUnit.MMHG))
        // 1013.25 hPa -> 29.92 inHg
        assertEquals("29.92 inHg", UnitConverters.formatPressure(1013.25, PressureUnit.INHG))
    }

    @Test
    fun testPrecipitationConversion() {
        assertEquals("12.5 mm", UnitConverters.formatPrecipitation(12.5, PrecipitationUnit.MILLIMETERS))
        assertEquals("0.98 in", UnitConverters.formatPrecipitation(25.0, PrecipitationUnit.INCHES))
    }

    @Test
    fun testVisibilityConversion() {
        assertEquals("10.0 km", UnitConverters.formatVisibility(10.0, VisibilityUnit.KILOMETERS))
        assertEquals("6.2 mi", UnitConverters.formatVisibility(10.0, VisibilityUnit.MILES))
    }

    @Test
    fun testDegreesToCardinal() {
        assertEquals("N", UnitConverters.degreesToCardinal(0))
        assertEquals("N", UnitConverters.degreesToCardinal(360))
        assertEquals("NE", UnitConverters.degreesToCardinal(45))
        assertEquals("E", UnitConverters.degreesToCardinal(90))
        assertEquals("SE", UnitConverters.degreesToCardinal(135))
        assertEquals("S", UnitConverters.degreesToCardinal(180))
        assertEquals("SW", UnitConverters.degreesToCardinal(225))
        assertEquals("W", UnitConverters.degreesToCardinal(270))
        assertEquals("NW", UnitConverters.degreesToCardinal(315))
    }
}
