package com.weatherglass.app.feature.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherglass.app.core.preferences.UserPreferencesRepository
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.WeatherReport
import com.weatherglass.app.domain.repository.CitySearchRepository
import com.weatherglass.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageCitiesViewModel(
    private val citySearchRepository: CitySearchRepository,
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val favoriteCities: StateFlow<List<CityLocation>> = citySearchRepository.getFavoriteCities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _cityWeatherMap = MutableStateFlow<Map<String, WeatherReport>>(emptyMap())
    val cityWeatherMap: StateFlow<Map<String, WeatherReport>> = _cityWeatherMap.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteCities.collectLatest { list ->
                list.forEach { city ->
                    if (!_cityWeatherMap.value.containsKey(city.id)) {
                        loadCityWeather(city)
                    }
                }
            }
        }
    }

    private fun loadCityWeather(city: CityLocation) {
        viewModelScope.launch {
            val report = weatherRepository.getCachedWeather(city.id)
            if (report != null) {
                _cityWeatherMap.value = _cityWeatherMap.value + (city.id to report)
            } else {
                weatherRepository.getWeather(city, forceRefresh = false).getOrNull()?.let { fresh ->
                    _cityWeatherMap.value = _cityWeatherMap.value + (city.id to fresh)
                }
            }
        }
    }

    fun removeCity(cityId: String) {
        viewModelScope.launch {
            if (favoriteCities.value.size > 1) {
                citySearchRepository.removeFavorite(cityId)
            }
        }
    }

    class Factory(
        private val citySearchRepository: CitySearchRepository,
        private val weatherRepository: WeatherRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageCitiesViewModel(
                citySearchRepository,
                weatherRepository,
                preferencesRepository
            ) as T
        }
    }
}
