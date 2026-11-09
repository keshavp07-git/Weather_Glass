package com.weatherglass.app

import android.app.Application
import com.weatherglass.app.data.cities.CitySearchRepositoryImpl
import com.weatherglass.app.di.AppContainer
import com.weatherglass.app.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WeatherGlassApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        // Prepopulate starter cities on first run
        applicationScope.launch {
            (container.citySearchRepository as? CitySearchRepositoryImpl)?.ensureDefaultFavoritesIfEmpty()
        }

        // Initialize Notification Channel for automatic weather alerts & forecasts
        com.weatherglass.app.core.notification.WeatherNotificationHelper.createNotificationChannel(this)

        // Schedule periodic background refresh
        com.weatherglass.app.core.work.WeatherRefreshWorker.schedule(this, 60)
    }
}
