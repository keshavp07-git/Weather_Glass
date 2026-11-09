package com.weatherglass.app.data.weather.weatherapi

import com.weatherglass.app.core.common.DateTimeFormatters
import com.weatherglass.app.data.weather.provider.ProviderCapabilities
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.data.weather.weatherapi.api.WeatherApiRestApi
import com.weatherglass.app.domain.model.AirQuality
import com.weatherglass.app.domain.model.AirQualityCategory
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.CurrentWeather
import com.weatherglass.app.domain.model.DailyForecast
import com.weatherglass.app.domain.model.HourlyForecast
import com.weatherglass.app.domain.model.SunMoon
import com.weatherglass.app.domain.model.WeatherAlert
import com.weatherglass.app.domain.model.WeatherReport
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherApiProvider(
    private val api: WeatherApiRestApi,
    private var apiKey: String = "ce4c103b27064f1bb41133356260911"
) : WeatherProvider {

    override val id: String = "weather_api"
    override val displayName: String = "WeatherAPI.com"
    override val description: String = "Global forecasting, AQI, and official meteorological alerts powered by WeatherAPI."
    override val capabilities: ProviderCapabilities = ProviderCapabilities(
        supportsCurrent = true,
        supportsHourly = true,
        supportsDaily = true,
        supportsAirQuality = true,
        supportsAlerts = true,
        requiresApiKey = true,
        maxDailyDays = 7
    )

    fun setApiKey(key: String) {
        apiKey = key
    }

    override fun isConfigured(): Boolean = apiKey.isNotBlank()

    override suspend fun fetchWeather(location: CityLocation): WeatherReport {
        if (!isConfigured()) {
            throw IllegalStateException("WeatherAPI key is missing.")
        }

        val query = "${location.latitude},${location.longitude}"
        val response = api.getForecast(
            key = apiKey,
            query = query,
            days = 7,
            aqi = "yes",
            alerts = "yes"
        )

        val currentDto = response.current ?: throw IllegalStateException("Current weather unavailable from WeatherAPI")
        val isDay = currentDto.isDay == 1
        val code = currentDto.condition?.code ?: 1000
        val (condition, defaultDesc) = WeatherApiCodeMapper.mapWeatherCode(code, isDay)
        val conditionDesc = currentDto.condition?.text?.ifBlank { defaultDesc } ?: defaultDesc

        val forecastContainer = response.forecast
        val firstDay = forecastContainer?.forecastday?.firstOrNull()?.day

        val highTemp = firstDay?.maxtempC ?: currentDto.tempC
        val lowTemp = firstDay?.mintempC ?: currentDto.tempC

        val current = CurrentWeather(
            temperatureCelsius = currentDto.tempC,
            feelsLikeCelsius = currentDto.feelslikeC,
            condition = condition,
            conditionDescription = conditionDesc,
            highTempCelsius = highTemp,
            lowTempCelsius = lowTemp,
            humidityPercent = currentDto.humidity,
            windSpeedKmh = currentDto.windKph,
            windDirectionDegrees = currentDto.windDegree,
            windGustsKmh = currentDto.gustKph,
            pressureHpa = currentDto.pressureMb,
            visibilityKm = currentDto.visKm,
            cloudCoverPercent = currentDto.cloud,
            dewPointCelsius = currentDto.dewpointC,
            uvIndex = currentDto.uv,
            precipitationMm = currentDto.precipMm,
            rainProbabilityPercent = firstDay?.dailyChanceOfRain ?: 0,
            isDay = isDay
        )

        // Hourly
        val nowEpoch = System.currentTimeMillis() / 1000
        val hourlyList = mutableListOf<HourlyForecast>()
        val allHours = forecastContainer?.forecastday?.flatMap { it.hour } ?: emptyList()
        var highlightedNow = false

        for (hourDto in allHours) {
            val epoch = hourDto.timeEpoch
            if (epoch >= nowEpoch - 3600 && hourlyList.size < 24) {
                val hourIsDay = hourDto.isDay == 1
                val (hourCond, _) = WeatherApiCodeMapper.mapWeatherCode(hourDto.condition?.code ?: 1000, hourIsDay)
                val isNow = !highlightedNow && kotlin.math.abs(epoch - nowEpoch) < 3600
                if (isNow) highlightedNow = true

                val formattedTime = if (isNow) "Now" else {
                    runCatching {
                        val instant = Instant.ofEpochSecond(epoch)
                        val ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        ldt.format(DateTimeFormatter.ofPattern("h a"))
                    }.getOrDefault("12 PM")
                }

                hourlyList.add(
                    HourlyForecast(
                        epochSeconds = epoch,
                        formattedTime = formattedTime,
                        temperatureCelsius = hourDto.tempC,
                        condition = hourCond,
                        precipitationProbabilityPercent = hourDto.chanceOfRain,
                        precipitationMm = hourDto.precipMm,
                        windSpeedKmh = hourDto.windKph,
                        windDirectionDegrees = hourDto.windDegree,
                        humidityPercent = hourDto.humidity,
                        isNow = isNow
                    )
                )
            }
        }

        // Daily
        val dailyList = mutableListOf<DailyForecast>()
        val forecastDays = forecastContainer?.forecastday ?: emptyList()
        forecastDays.take(7).forEachIndexed { index, fDay ->
            val epoch = fDay.dateEpoch
            val dayData = fDay.day
            val astro = fDay.astro
            val (dayCond, defaultDayDesc) = WeatherApiCodeMapper.mapWeatherCode(dayData?.condition?.code ?: 1000, true)
            val dayDesc = dayData?.condition?.text?.ifBlank { defaultDayDesc } ?: defaultDayDesc
            val dayName = if (index == 0) "Today" else DateTimeFormatters.formatDayOfWeek(epoch)

            dailyList.add(
                DailyForecast(
                    epochSeconds = epoch,
                    dateIso = fDay.date,
                    dayName = dayName,
                    condition = dayCond,
                    conditionDescription = dayDesc,
                    maxTempCelsius = dayData?.maxtempC ?: 0.0,
                    minTempCelsius = dayData?.mintempC ?: 0.0,
                    precipitationProbabilityPercent = dayData?.dailyChanceOfRain ?: 0,
                    precipitationMm = dayData?.totalprecipMm ?: 0.0,
                    snowfallCm = dayData?.totalsnowCm ?: 0.0,
                    uvIndexMax = dayData?.uv ?: 0.0,
                    sunrise = astro?.sunrise ?: "06:00 AM",
                    sunset = astro?.sunset ?: "06:00 PM"
                )
            )
        }

        // Air Quality - Computed via official standard breakpoints matching AQI.in & EPA
        val airQuality = currentDto.airQuality?.let { aqDto ->
            val aqiVal = com.weatherglass.app.core.common.AqiCalculator.calculateAqiFromPm25(aqDto.pm25)
            val category = com.weatherglass.app.core.common.AqiCalculator.categoryFromAqi(aqiVal)

            AirQuality(
                aqi = aqiVal,
                category = category,
                pm25 = aqDto.pm25,
                pm10 = aqDto.pm10,
                co = aqDto.co,
                no2 = aqDto.no2,
                so2 = aqDto.so2,
                o3 = aqDto.o3,
                isAvailable = true
            )
        }

        // Sun & Moon
        val firstAstro = forecastDays.firstOrNull()?.astro
        val sunMoon = SunMoon(
            sunriseTime = firstAstro?.sunrise ?: "06:20 AM",
            sunsetTime = firstAstro?.sunset ?: "05:18 PM",
            daylightDurationMinutes = 660,
            moonriseTime = firstAstro?.moonrise ?: "",
            moonsetTime = firstAstro?.moonset ?: "",
            moonPhaseName = firstAstro?.moonPhase ?: "Waxing Crescent",
            moonIlluminationPercent = firstAstro?.moonIllumination ?: 50
        )

        // Alerts
        val alerts = response.alerts?.alert?.map { alertDto ->
            WeatherAlert(
                id = "${alertDto.event}_${alertDto.headline.hashCode()}",
                title = alertDto.headline.ifBlank { alertDto.event },
                severity = alertDto.severity,
                description = alertDto.desc,
                issuingAuthority = "Official Meteorological Service",
                effectiveTime = "",
                expiryTime = "",
                precautions = alertDto.instruction
            )
        } ?: emptyList()

        return WeatherReport(
            location = location,
            current = current,
            hourly = hourlyList,
            daily = dailyList,
            airQuality = airQuality,
            sunMoon = sunMoon,
            alerts = alerts,
            lastUpdatedEpochSeconds = nowEpoch,
            isCached = false,
            providerId = id,
            providerName = displayName
        )
    }
}
