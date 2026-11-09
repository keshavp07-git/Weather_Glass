package com.weatherglass.app.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.LiquidGlassCard
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CityLocation

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onCitySelected: (CityLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val favoriteCities by viewModel.favoriteCities.collectAsState()
    val focusManager = LocalFocusManager.current

    val favoriteIds = favoriteCities.map { it.id }.toSet()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GlassColors.TextPrimary
                    )
                }

                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Search city or country...",
                            style = WeatherGlassTypography.bodyMedium,
                            color = GlassColors.TextTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GlassColors.TextTertiary
                        )
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = GlassColors.TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x381E293B),
                        unfocusedContainerColor = Color(0x281E293B),
                        focusedBorderColor = GlassColors.AccentCyan,
                        unfocusedBorderColor = GlassColors.GlassBorderSubtle,
                        focusedTextColor = GlassColors.TextPrimary,
                        unfocusedTextColor = GlassColors.TextPrimary
                    )
                )
            }

            if (uiState.isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlassColors.AccentCyan)
                }
            } else if (uiState.query.isNotBlank()) {
                // Results List
                if (uiState.searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No locations found matching \"${uiState.query}\"",
                            style = WeatherGlassTypography.bodyMedium,
                            color = GlassColors.TextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchResults, key = { it.id }) { city ->
                            val isFav = favoriteIds.contains(city.id)
                            SearchResultItem(
                                city = city,
                                isFavorite = isFav,
                                onCityClick = {
                                    viewModel.onCitySelected(city)
                                    onCitySelected(city)
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(city) }
                            )
                        }
                    }
                }
            } else {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT SEARCHES",
                            style = WeatherGlassTypography.labelLarge,
                            color = GlassColors.TextTertiary
                        )
                        TextButton(onClick = { viewModel.clearRecentSearches() }) {
                            Text(
                                text = "Clear",
                                style = WeatherGlassTypography.labelSmall,
                                color = GlassColors.AccentCyan
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentSearches, key = { it.id }) { recentCity ->
                            val isFav = favoriteIds.contains(recentCity.id)
                            SearchResultItem(
                                city = recentCity,
                                isFavorite = isFav,
                                isRecent = true,
                                onCityClick = {
                                    viewModel.onCitySelected(recentCity)
                                    onCitySelected(recentCity)
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(recentCity) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    city: CityLocation,
    isFavorite: Boolean,
    isRecent: Boolean = false,
    onCityClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCityClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRecent) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GlassColors.TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column {
                    Text(
                        text = city.name,
                        style = WeatherGlassTypography.titleLarge.copy(fontSize = 16.sp),
                        color = GlassColors.TextPrimary
                    )
                    val subtitle = listOf(city.region, city.country).filter { it.isNotBlank() }.joinToString(", ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = WeatherGlassTypography.bodyMedium.copy(fontSize = 12.sp),
                            color = GlassColors.TextSecondary
                        )
                    }
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove Favorite" else "Add Favorite",
                    tint = if (isFavorite) GlassColors.AccentGold else GlassColors.TextTertiary
                )
            }
        }
    }
}
