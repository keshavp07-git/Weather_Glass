package com.weatherglass.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherglass.app.core.preferences.UserPreferencesRepository
import com.weatherglass.app.data.weather.provider.WeatherProvider
import com.weatherglass.app.domain.model.AppTheme
import com.weatherglass.app.domain.model.PrecipitationUnit
import com.weatherglass.app.domain.model.PressureUnit
import com.weatherglass.app.domain.model.TemperatureUnit
import com.weatherglass.app.domain.model.TimeFormat
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.VisibilityUnit
import com.weatherglass.app.domain.model.WindSpeedUnit
import com.weatherglass.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val availableProviders: List<WeatherProvider> = weatherRepository.getAvailableProviders()

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { preferencesRepository.updateTemperatureUnit(unit) }
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch { preferencesRepository.updateWindSpeedUnit(unit) }
    }

    fun updatePressureUnit(unit: PressureUnit) {
        viewModelScope.launch { preferencesRepository.updatePressureUnit(unit) }
    }

    fun updatePrecipitationUnit(unit: PrecipitationUnit) {
        viewModelScope.launch { preferencesRepository.updatePrecipitationUnit(unit) }
    }

    fun updateVisibilityUnit(unit: VisibilityUnit) {
        viewModelScope.launch { preferencesRepository.updateVisibilityUnit(unit) }
    }

    fun updateTimeFormat(format: TimeFormat) {
        viewModelScope.launch { preferencesRepository.updateTimeFormat(format) }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch { preferencesRepository.updateTheme(theme) }
    }

    fun updateAnimations(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateAnimationsEnabled(enabled) }
    }

    fun updateSelectedProvider(providerId: String) {
        viewModelScope.launch {
            preferencesRepository.updateSelectedProvider(providerId)
            weatherRepository.setActiveProvider(providerId)
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(preferencesRepository, weatherRepository) as T
        }
    }
}
