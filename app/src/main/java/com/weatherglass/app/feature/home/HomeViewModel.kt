package com.weatherglass.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.core.location.LocationProvider
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val cities: List<CityLocation> = emptyList(),
    val currentCityIndex: Int = 0,
    val weatherReports: Map<String, WeatherReport> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val citySearchRepository: CitySearchRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val targetCityId = MutableStateFlow<String?>(null)

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    init {
        observeFavoritesAndPreferences()
    }

    private fun observeFavoritesAndPreferences() {
        viewModelScope.launch {
            citySearchRepository.getFavoriteCities().collectLatest { favoriteCities ->
                if (favoriteCities.isNotEmpty()) {
                    val pendingTargetId = targetCityId.value
                    val targetIndex = if (pendingTargetId != null) {
                        favoriteCities.indexOfFirst { it.id == pendingTargetId }.takeIf { it != -1 }
                    } else null

                    val nextIndex = targetIndex ?: _uiState.value.currentCityIndex.coerceIn(0, favoriteCities.size - 1)
                    if (targetIndex != null) {
                        targetCityId.value = null
                    }

                    _uiState.update { current ->
                        current.copy(cities = favoriteCities, currentCityIndex = nextIndex)
                    }

                    val activeCity = favoriteCities.getOrNull(nextIndex)
                    if (activeCity != null) {
                        loadWeatherForCity(activeCity)
                    }
                    prefetchAdjacentCities(favoriteCities, nextIndex)
                }
            }
        }

        viewModelScope.launch {
            // Check current location weather if permission available
            if (locationProvider.hasLocationPermission()) {
                val currentLoc = locationProvider.getCurrentLocation()
                if (currentLoc != null) {
                    citySearchRepository.addFavorite(currentLoc)
                }
            }
        }
    }

    fun onPageChanged(newIndex: Int) {
        val cities = _uiState.value.cities
        if (newIndex in cities.indices) {
            _uiState.update { it.copy(currentCityIndex = newIndex) }
            val city = cities[newIndex]
            loadWeatherForCity(city)
            prefetchAdjacentCities(cities, newIndex)
            viewModelScope.launch {
                preferencesRepository.updateLastSelectedCityId(city.id)
            }
        }
    }

    fun selectOrAddCity(city: CityLocation) {
        viewModelScope.launch {
            targetCityId.value = city.id
            citySearchRepository.saveRecentSearch(city)
            citySearchRepository.addFavorite(city.copy(isFavorite = true))
            loadWeatherForCity(city, forceRefresh = false)
            preferencesRepository.updateLastSelectedCityId(city.id)
        }
    }

    fun loadWeatherForCity(city: CityLocation, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.weatherReports.containsKey(city.id) && !forceRefresh) {
                return@launch
            }

            _uiState.update { it.copy(isLoading = !it.weatherReports.containsKey(city.id), isRefreshing = forceRefresh) }

            when (val result = weatherRepository.getWeather(city, forceRefresh = forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        val updatedMap = current.weatherReports.toMutableMap()
                        updatedMap[city.id] = result.data
                        current.copy(
                            weatherReports = updatedMap,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = if (current.weatherReports.containsKey(city.id)) null else result.message
                        )
                    }
                }
                is AppResult.Loading -> Unit
            }
        }
    }

    private fun prefetchAdjacentCities(cities: List<CityLocation>, currentIndex: Int) {
        val prevIndex = currentIndex - 1
        val nextIndex = currentIndex + 1

        if (prevIndex >= 0) {
            val prevCity = cities[prevIndex]
            if (!_uiState.value.weatherReports.containsKey(prevCity.id)) {
                viewModelScope.launch { weatherRepository.getWeather(prevCity, forceRefresh = false) }
            }
        }

        if (nextIndex < cities.size) {
            val nextCity = cities[nextIndex]
            if (!_uiState.value.weatherReports.containsKey(nextCity.id)) {
                viewModelScope.launch { weatherRepository.getWeather(nextCity, forceRefresh = false) }
            }
        }
    }

    fun refreshCurrentCity() {
        val currentCity = _uiState.value.cities.getOrNull(_uiState.value.currentCityIndex) ?: return
        loadWeatherForCity(currentCity, forceRefresh = true)
    }

    class Factory(
        private val weatherRepository: WeatherRepository,
        private val citySearchRepository: CitySearchRepository,
        private val preferencesRepository: UserPreferencesRepository,
        private val locationProvider: LocationProvider
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                weatherRepository,
                citySearchRepository,
                preferencesRepository,
                locationProvider
            ) as T
        }
    }
}
