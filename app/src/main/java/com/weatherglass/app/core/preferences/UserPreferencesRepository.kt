package com.weatherglass.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weatherglass.app.domain.model.AppTheme
import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.TimeFormat
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_glass_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val PRESSURE_UNIT = stringPreferencesKey("pressure_unit")
        val PRECIPITATION_UNIT = stringPreferencesKey("precipitation_unit")
        val VISIBILITY_UNIT = stringPreferencesKey("visibility_unit")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val THEME = stringPreferencesKey("app_theme")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val SELECTED_PROVIDER = stringPreferencesKey("selected_provider")
        val AUTO_REFRESH_MINUTES = intPreferencesKey("auto_refresh_minutes")
        val LAST_SELECTED_CITY_ID = stringPreferencesKey("last_selected_city_id")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val tempUnit = preferences[PreferencesKeys.TEMPERATURE_UNIT]?.let {
                runCatching { TemperatureUnit.valueOf(it) }.getOrNull()
            } ?: TemperatureUnit.CELSIUS

            val windUnit = preferences[PreferencesKeys.WIND_SPEED_UNIT]?.let {
                runCatching { WindSpeedUnit.valueOf(it) }.getOrNull()
            } ?: WindSpeedUnit.KMH

            val pressureUnit = preferences[PreferencesKeys.PRESSURE_UNIT]?.let {
                runCatching { PressureUnit.valueOf(it) }.getOrNull()
            } ?: PressureUnit.HPA

            val precipUnit = preferences[PreferencesKeys.PRECIPITATION_UNIT]?.let {
                runCatching { PrecipitationUnit.valueOf(it) }.getOrNull()
            } ?: PrecipitationUnit.MILLIMETERS

            val visUnit = preferences[PreferencesKeys.VISIBILITY_UNIT]?.let {
                runCatching { VisibilityUnit.valueOf(it) }.getOrNull()
            } ?: VisibilityUnit.KILOMETERS

            val timeFormat = preferences[PreferencesKeys.TIME_FORMAT]?.let {
                runCatching { TimeFormat.valueOf(it) }.getOrNull()
            } ?: TimeFormat.TWELVE_HOUR

            val theme = preferences[PreferencesKeys.THEME]?.let {
                runCatching { AppTheme.valueOf(it) }.getOrNull()
            } ?: AppTheme.DARK

            val animationsEnabled = preferences[PreferencesKeys.ANIMATIONS_ENABLED] ?: true
            val providerId = preferences[PreferencesKeys.SELECTED_PROVIDER] ?: "open_meteo"
            val refreshMins = preferences[PreferencesKeys.AUTO_REFRESH_MINUTES] ?: 30

            UserPreferences(
                temperatureUnit = tempUnit,
                windSpeedUnit = windUnit,
                pressureUnit = pressureUnit,
                precipitationUnit = precipUnit,
                visibilityUnit = visUnit,
                timeFormat = timeFormat,
                theme = theme,
                animationsEnabled = animationsEnabled,
                selectedProviderId = providerId,
                autoRefreshMinutes = refreshMins
            )
        }

    val lastSelectedCityIdFlow: Flow<String?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.LAST_SELECTED_CITY_ID] }

    suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[PreferencesKeys.TEMPERATURE_UNIT] = unit.name }
    }

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { it[PreferencesKeys.WIND_SPEED_UNIT] = unit.name }
    }

    suspend fun updatePressureUnit(unit: PressureUnit) {
        context.dataStore.edit { it[PreferencesKeys.PRESSURE_UNIT] = unit.name }
    }

    suspend fun updatePrecipitationUnit(unit: PrecipitationUnit) {
        context.dataStore.edit { it[PreferencesKeys.PRECIPITATION_UNIT] = unit.name }
    }

    suspend fun updateVisibilityUnit(unit: VisibilityUnit) {
        context.dataStore.edit { it[PreferencesKeys.VISIBILITY_UNIT] = unit.name }
    }

    suspend fun updateTimeFormat(format: TimeFormat) {
        context.dataStore.edit { it[PreferencesKeys.TIME_FORMAT] = format.name }
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { it[PreferencesKeys.THEME] = theme.name }
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ANIMATIONS_ENABLED] = enabled }
    }

    suspend fun updateSelectedProvider(providerId: String) {
        context.dataStore.edit { it[PreferencesKeys.SELECTED_PROVIDER] = providerId }
    }

    suspend fun updateLastSelectedCityId(cityId: String) {
        context.dataStore.edit { it[PreferencesKeys.LAST_SELECTED_CITY_ID] = cityId }
    }
}
