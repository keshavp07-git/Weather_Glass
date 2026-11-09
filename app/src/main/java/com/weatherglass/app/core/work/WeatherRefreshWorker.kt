package com.weatherglass.app.core.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.weatherglass.app.WeatherGlassApplication
import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.notification.WeatherNotificationHelper
import com.weatherglass.app.domain.model.WeatherReport
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class WeatherRefreshWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? WeatherGlassApplication ?: return Result.failure()
        val container = app.container

        val favoriteCities = container.citySearchRepository.getFavoriteCities().firstOrNull() ?: return Result.success()

        var primaryReport: WeatherReport? = null

        for ((index, city) in favoriteCities.withIndex()) {
            val result = runCatching {
                container.weatherRepository.getWeather(city, forceRefresh = true)
            }.getOrNull()

            if (index == 0 && result is AppResult.Success) {
                primaryReport = result.data
            }
        }

        // Send automatic periodic weather notification for the primary location
        if (primaryReport != null) {
            WeatherNotificationHelper.showWeatherNotification(applicationContext, primaryReport)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "periodic_weather_refresh"

        fun schedule(context: Context, intervalMinutes: Long = 60) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(
                intervalMinutes.coerceAtLeast(15),
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
