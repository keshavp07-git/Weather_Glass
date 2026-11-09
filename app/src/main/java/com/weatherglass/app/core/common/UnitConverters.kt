package com.weatherglass.app.core.common

import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit
import java.util.Locale
import kotlin.math.roundToInt

object UnitConverters {

    // Temperature (Base is Celsius)
    fun formatTemperature(celsius: Double, unit: TemperatureUnit, includeUnit: Boolean = true): String {
        val converted = when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
        val rounded = converted.roundToInt()
        return if (includeUnit) "$rounded°" else "$rounded"
    }

    fun convertTemperature(celsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
    }

    // Wind Speed (Base is km/h)
    fun formatWindSpeed(kmh: Double, unit: WindSpeedUnit): String {
        val converted = when (unit) {
            WindSpeedUnit.KMH -> kmh
            WindSpeedUnit.MS -> kmh / 3.6
            WindSpeedUnit.MPH -> kmh * 0.621371
            WindSpeedUnit.KNOTS -> kmh * 0.539957
        }
        return String.format(Locale.US, "%.1f %s", converted, unit.symbol)
    }

    // Pressure (Base is hPa)
    fun formatPressure(hpa: Double, unit: PressureUnit): String {
        val converted = when (unit) {
            PressureUnit.HPA -> hpa
            PressureUnit.MMHG -> hpa * 0.750062
            PressureUnit.INHG -> hpa * 0.02953
        }
        return when (unit) {
            PressureUnit.INHG -> String.format(Locale.US, "%.2f %s", converted, unit.symbol)
            else -> "${converted.roundToInt()} ${unit.symbol}"
        }
    }

    // Precipitation (Base is mm)
    fun formatPrecipitation(mm: Double, unit: PrecipitationUnit): String {
        return when (unit) {
            PrecipitationUnit.MILLIMETERS -> String.format(Locale.US, "%.1f mm", mm)
            PrecipitationUnit.INCHES -> String.format(Locale.US, "%.2f in", mm * 0.0393701)
        }
    }

    // Visibility (Base is km)
    fun formatVisibility(km: Double, unit: VisibilityUnit): String {
        return when (unit) {
            VisibilityUnit.KILOMETERS -> String.format(Locale.US, "%.1f km", km)
            VisibilityUnit.MILES -> String.format(Locale.US, "%.1f mi", km * 0.621371)
        }
    }

    // Wind Direction Compass
    fun degreesToCardinal(degrees: Int): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (((degrees % 360) + 360) % 360 + 11.25) / 22.5
        return directions[index.toInt() % 16]
    }
}
