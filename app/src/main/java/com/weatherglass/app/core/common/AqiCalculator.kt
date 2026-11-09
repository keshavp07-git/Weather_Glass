package com.weatherglass.app.core.common

import com.weatherglass.app.domain.model.AirQualityCategory
import kotlin.math.roundToInt

object AqiCalculator {

    /**
     * Standard US EPA Air Quality Index (0-500) computed from PM2.5 in µg/m³.
     * Matches AQI.in, Google Air Quality, AirVisual, and CPCB reference scales.
     */
    fun calculateAqiFromPm25(pm25: Double): Int {
        val c = pm25.coerceAtLeast(0.0)
        return when {
            c <= 12.0 -> ((50.0 - 0.0) / (12.0 - 0.0) * (c - 0.0) + 0.0).roundToInt()
            c <= 35.4 -> ((100.0 - 51.0) / (35.4 - 12.1) * (c - 12.1) + 51.0).roundToInt()
            c <= 55.4 -> ((150.0 - 101.0) / (55.4 - 35.5) * (c - 35.5) + 101.0).roundToInt()
            c <= 150.4 -> ((200.0 - 151.0) / (150.4 - 55.5) * (c - 55.5) + 151.0).roundToInt()
            c <= 250.4 -> ((300.0 - 201.0) / (250.4 - 150.5) * (c - 150.5) + 201.0).roundToInt()
            c <= 350.4 -> ((400.0 - 301.0) / (350.4 - 250.5) * (c - 250.5) + 301.0).roundToInt()
            c <= 500.4 -> ((500.0 - 401.0) / (500.4 - 350.5) * (c - 350.5) + 401.0).roundToInt()
            else -> 500
        }
    }

    fun categoryFromAqi(aqi: Int): AirQualityCategory {
        return when (aqi) {
            in 0..50 -> AirQualityCategory.GOOD
            in 51..100 -> AirQualityCategory.FAIR
            in 101..150 -> AirQualityCategory.MODERATE
            in 151..200 -> AirQualityCategory.POOR
            in 201..300 -> AirQualityCategory.VERY_POOR
            else -> AirQualityCategory.EXTREMELY_POOR
        }
    }
}
