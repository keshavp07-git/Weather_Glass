package com.weatherglass.app.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherglass.app.core.common.AppResult
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.domain.repository.CitySearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: List<CityLocation> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val citySearchRepository: CitySearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    val recentSearches: StateFlow<List<CityLocation>> = citySearchRepository.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteCities: StateFlow<List<CityLocation>> = citySearchRepository.getFavoriteCities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(350)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false, errorMessage = null) }
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }
        when (val result = citySearchRepository.searchCities(query)) {
            is AppResult.Success -> {
                _uiState.update { it.copy(searchResults = result.data, isSearching = false, errorMessage = null) }
            }
            is AppResult.Error -> {
                _uiState.update { it.copy(isSearching = false, errorMessage = result.message) }
            }
            is AppResult.Loading -> {
                _uiState.update { it.copy(isSearching = true) }
            }
        }
    }

    fun onCitySelected(city: CityLocation) {
        viewModelScope.launch {
            citySearchRepository.saveRecentSearch(city)
        }
    }

    fun toggleFavorite(city: CityLocation) {
        viewModelScope.launch {
            val isFav = citySearchRepository.isFavorite(city.id)
            if (isFav) {
                citySearchRepository.removeFavorite(city.id)
            } else {
                citySearchRepository.addFavorite(city.copy(isFavorite = true))
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            citySearchRepository.clearRecentSearches()
        }
    }

    class Factory(private val citySearchRepository: CitySearchRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(citySearchRepository) as T
        }
    }
}
