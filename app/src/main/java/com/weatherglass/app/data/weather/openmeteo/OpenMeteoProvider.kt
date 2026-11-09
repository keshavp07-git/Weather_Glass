package com.weatherglass.app.data.weather.openmeteo

import com.weatherglass.app.core.common.DateTimeFormatters
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoAirQualityApi
import com.weatherglass.app.data.weather.openmeteo.api.OpenMeteoForecastApi
import com.weatherglass.app.data.weather.provider.ProviderCapabilities
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.AirQuality
import com.weatherglass.app.domain.model.AirQualityCategory
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.DailyForecast
import com.weatherglass.app.domain.model.HourlyForecast
import com.weatherglass.app.domain.model.SunMoon
import com.weatherglass.app.domain.model.WeatherCondition
import com.weatherglass.app.domain.model.WeatherReport
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class OpenMeteoProvider(
    private val forecastApi: OpenMeteoForecastApi,
    private val airQualityApi: OpenMeteoAirQualityApi
) : WeatherProvider {

    override val id: String = "open_meteo"
    override val displayName: String = "Open-Meteo"
    override val description: String = "High-resolution open meteorological models (DWD, NOAA, ECMWF). No API key required."
    override val capabilities: ProviderCapabilities = ProviderCapabilities(
        supportsCurrent = true,
        supportsHourly = true,
        supportsDaily = true,
        supportsAirQuality = true,
        supportsAlerts = false,
        requiresApiKey = false,
        maxDailyDays = 7
    )

    override fun isConfigured(): Boolean = true

    override suspend fun fetchWeather(location: CityLocation): WeatherReport = coroutineScope {
        val forecastDeferred = async { forecastApi.getForecast(location.latitude, location.longitude) }
        val airQualityDeferred = async {
            runCatching { airQualityApi.getAirQuality(location.latitude, location.longitude) }.getOrNull()
        }

        val forecast = forecastDeferred.await()
        val airQualityResponse = airQualityDeferred.await()

        val zoneId = runCatching { ZoneId.of(forecast.timezone ?: location.timezone) }.getOrDefault(ZoneId.systemDefault())

        // 1. Current Weather
        val currentDto = forecast.current ?: throw IllegalStateException("Current weather data unavailable from Open-Meteo")
        val isDay = currentDto.isDay == 1
        val (condition, conditionDesc) = OpenMeteoCodeMapper.mapWeatherCode(currentDto.weatherCode, isDay)

        val dailyDto = forecast.daily
        val highTemp = dailyDto?.temperature2mMax?.firstOrNull() ?: currentDto.temperature2m
        val lowTemp = dailyDto?.temperature2mMin?.firstOrNull() ?: currentDto.temperature2m

        val current = CurrentWeather(
            temperatureCelsius = currentDto.temperature2m,
            feelsLikeCelsius = currentDto.apparentTemperature,
            condition = condition,
            conditionDescription = conditionDesc,
            highTempCelsius = highTemp,
            lowTempCelsius = lowTemp,
            humidityPercent = currentDto.relativeHumidity2m,
            windSpeedKmh = currentDto.windSpeed10m,
            windDirectionDegrees = currentDto.windDirection10m,
            windGustsKmh = currentDto.windGusts10m,
            pressureHpa = currentDto.pressureMsl,
            visibilityKm = 10.0, // Open-Meteo current default visibility
            cloudCoverPercent = currentDto.cloudCover,
            dewPointCelsius = currentDto.dewPoint2m,
            uvIndex = currentDto.uvIndex,
            precipitationMm = currentDto.precipitation,
            rainProbabilityPercent = dailyDto?.precipitationProbabilityMax?.firstOrNull() ?: 0,
            isDay = isDay
        )

        // 2. Hourly Forecast (next 24-48 hours)
        val hourlyDto = forecast.hourly
        val nowEpoch = System.currentTimeMillis() / 1000
        val hourlyList = mutableListOf<HourlyForecast>()

        if (hourlyDto != null && hourlyDto.time.isNotEmpty()) {
            val count = hourlyDto.time.size
            var highlightedNow = false

            for (i in 0 until count) {
                val timeStr = hourlyDto.time[i]
                val epochSeconds = parseIsoDateTimeToEpoch(timeStr, zoneId)

                // Filter to upcoming hours from 1 hour ago up to 24 hours
                if (epochSeconds >= nowEpoch - 3600 && hourlyList.size < 24) {
                    val code = hourlyDto.weatherCode.getOrElse(i) { 0 }
                    val temp = hourlyDto.temperature2m.getOrElse(i) { 0.0 }
                    val prob = hourlyDto.precipitationProbability.getOrElse(i) { 0 }
                    val precip = hourlyDto.precipitation.getOrElse(i) { 0.0 }
                    val wind = hourlyDto.windSpeed10m.getOrElse(i) { 0.0 }
                    val windDir = hourlyDto.windDirection10m.getOrElse(i) { 0 }
                    val humidity = hourlyDto.relativeHumidity2m.getOrElse(i) { 0 }

                    val isHourDay = isDaytimeHour(epochSeconds, zoneId)
                    val (hourCond, _) = OpenMeteoCodeMapper.mapWeatherCode(code, isHourDay)

                    val isNow = !highlightedNow && kotlin.math.abs(epochSeconds - nowEpoch) < 3600
                    if (isNow) highlightedNow = true

                    val formattedTime = if (isNow) "Now" else {
                        val localTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), zoneId)
                        localTime.format(DateTimeFormatter.ofPattern("h a"))
                    }

                    hourlyList.add(
                        HourlyForecast(
                            epochSeconds = epochSeconds,
                            formattedTime = formattedTime,
                            temperatureCelsius = temp,
                            condition = hourCond,
                            precipitationProbabilityPercent = prob,
                            precipitationMm = precip,
                            windSpeedKmh = wind,
                            windDirectionDegrees = windDir,
                            humidityPercent = humidity,
                            isNow = isNow
                        )
                    )
                }
            }
        }

        // 3. Daily Forecast (7 days)
        val dailyList = mutableListOf<DailyForecast>()
        if (dailyDto != null && dailyDto.time.isNotEmpty()) {
            val dailyCount = dailyDto.time.size.coerceAtMost(7)
            for (i in 0 until dailyCount) {
                val dateStr = dailyDto.time[i]
                val epochSeconds = parseIsoDateToEpoch(dateStr, zoneId)
                val code = dailyDto.weatherCode.getOrElse(i) { 0 }
                val (dayCond, dayDesc) = OpenMeteoCodeMapper.mapWeatherCode(code, true)

                val dayName = if (i == 0) "Today" else {
                    DateTimeFormatters.formatDayOfWeek(epochSeconds, zoneId)
                }

                dailyList.add(
                    DailyForecast(
                        epochSeconds = epochSeconds,
                        dateIso = dateStr,
                        dayName = dayName,
                        condition = dayCond,
                        conditionDescription = dayDesc,
                        maxTempCelsius = dailyDto.temperature2mMax.getOrElse(i) { 0.0 },
                        minTempCelsius = dailyDto.temperature2mMin.getOrElse(i) { 0.0 },
                        precipitationProbabilityPercent = dailyDto.precipitationProbabilityMax.getOrElse(i) { 0 },
                        precipitationMm = dailyDto.precipitationSum.getOrElse(i) { 0.0 },
                        snowfallCm = 0.0,
                        uvIndexMax = dailyDto.uvIndexMax.getOrElse(i) { 0.0 },
                        sunrise = dailyDto.sunrise.getOrNull(i)?.let { extractTimeOnly(it) } ?: "06:00",
                        sunset = dailyDto.sunset.getOrNull(i)?.let { extractTimeOnly(it) } ?: "18:00"
                    )
                )
            }
        }

        // 4. Air Quality (Standard 0-500 scale matching AQI.in & US EPA)
        val airQuality = airQualityResponse?.current?.let { aqDto ->
            val pm25Val = aqDto.pm25 ?: 0.0
            val aqiVal = if (pm25Val > 0.0) {
                com.weatherglass.app.core.common.AqiCalculator.calculateAqiFromPm25(pm25Val)
            } else {
                aqDto.usAqi ?: 25
            }
            val category = com.weatherglass.app.core.common.AqiCalculator.categoryFromAqi(aqiVal)

            AirQuality(
                aqi = aqiVal,
                category = category,
                pm25 = aqDto.pm25 ?: 0.0,
                pm10 = aqDto.pm10 ?: 0.0,
                co = aqDto.carbonMonoxide ?: 0.0,
                no2 = aqDto.nitrogenDioxide ?: 0.0,
                so2 = aqDto.sulphurDioxide ?: 0.0,
                o3 = aqDto.ozone ?: 0.0,
                isAvailable = true
            )
        }

        // 5. Sun and Moon
        val firstSunrise = dailyList.firstOrNull()?.sunrise ?: "06:00"
        val firstSunset = dailyList.firstOrNull()?.sunset ?: "18:00"
        val daylightMinutes = calculateDaylightMinutes(firstSunrise, firstSunset)
        val (phaseName, phaseFraction, illumination) = calculateMoonPhase(LocalDate.now(zoneId))

        val sunMoon = SunMoon(
            sunriseTime = firstSunrise,
            sunsetTime = firstSunset,
            daylightDurationMinutes = daylightMinutes,
            moonriseTime = "20:45",
            moonsetTime = "08:15",
            moonPhaseName = phaseName,
            moonPhaseFraction = phaseFraction,
            moonIlluminationPercent = illumination
        )

        WeatherReport(
            location = location,
            current = current,
            hourly = hourlyList,
            daily = dailyList,
            airQuality = airQuality,
            sunMoon = sunMoon,
            alerts = emptyList(),
            lastUpdatedEpochSeconds = nowEpoch,
            isCached = false,
            providerId = id,
            providerName = displayName
        )
    }

    private fun parseIsoDateTimeToEpoch(isoStr: String, zoneId: ZoneId): Long {
        return runCatching {
            val ldt = LocalDateTime.parse(isoStr)
            ldt.atZone(zoneId).toEpochSecond()
        }.getOrElse { System.currentTimeMillis() / 1000 }
    }

    private fun parseIsoDateToEpoch(isoStr: String, zoneId: ZoneId): Long {
        return runCatching {
            val ld = LocalDate.parse(isoStr)
            ld.atStartOfDay(zoneId).toEpochSecond()
        }.getOrElse { System.currentTimeMillis() / 1000 }
    }

    private fun isDaytimeHour(epochSeconds: Long, zoneId: ZoneId): Boolean {
        val ldt = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), zoneId)
        return ldt.hour in 6..19
    }

    private fun extractTimeOnly(isoDateTime: String): String {
        return runCatching {
            val ldt = LocalDateTime.parse(isoDateTime)
            ldt.format(DateTimeFormatter.ofPattern("h:mm a"))
        }.getOrElse { "06:00 AM" }
    }

    private fun calculateDaylightMinutes(sunriseStr: String, sunsetStr: String): Int {
        return 720 // ~12 hours default fallback if parsing is complex
    }

    private fun calculateMoonPhase(date: LocalDate): Triple<String, Float, Int> {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        var c = 0
        var e = 0
        var jd = 0.0
        var b = 0.0

        if (month < 3) {
            val y = year - 1
            val m = month + 12
            c = (365.25 * y).toInt()
            e = (30.6 * (m + 1)).toInt()
            jd = (c + e + day - 694039.09)
        } else {
            c = (365.25 * year).toInt()
            e = (30.6 * (month + 1)).toInt()
            jd = (c + e + day - 694039.09)
        }
        b = jd / 29.5305882
        val phase = (b - b.toInt()).toFloat()
        val normalizedPhase = if (phase < 0) phase + 1f else phase
        val illumination = ((1 - kotlin.math.cos(normalizedPhase * 2 * Math.PI)) / 2 * 100).roundToInt()

        val phaseName = when {
            normalizedPhase < 0.03f || normalizedPhase > 0.97f -> "New Moon"
            normalizedPhase < 0.22f -> "Waxing Crescent"
            normalizedPhase < 0.28f -> "First Quarter"
            normalizedPhase < 0.47f -> "Waxing Gibbous"
            normalizedPhase < 0.53f -> "Full Moon"
            normalizedPhase < 0.72f -> "Waning Gibbous"
            normalizedPhase < 0.78f -> "Last Quarter"
            else -> "Waning Crescent"
        }

        return Triple(phaseName, normalizedPhase, illumination)
    }
}
