package com.weatherglass.app.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.preferences.UserPreferencesRepository
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.model.UserPreferences
import com.weatherglass.app.domain.model.WeatherReport
import com.weatherglass.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailsUiState(
    val report: WeatherReport? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailedWeatherViewModel(
    private val city: CityLocation,
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState(isLoading = true))
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    init {
        loadWeather()
    }

    fun loadWeather(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.report == null) }
            val cached = weatherRepository.getCachedWeather(city.id)
            if (cached != null && !forceRefresh) {
                _uiState.update { it.copy(report = cached, isLoading = false, errorMessage = null) }
                return@launch
            }

            when (val result = weatherRepository.getWeather(city, forceRefresh = forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(report = result.data, isLoading = false, errorMessage = null) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is AppResult.Loading -> Unit
            }
        }
    }

    class Factory(
        private val city: CityLocation,
        private val weatherRepository: WeatherRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailedWeatherViewModel(city, weatherRepository, preferencesRepository) as T
        }
    }
}
