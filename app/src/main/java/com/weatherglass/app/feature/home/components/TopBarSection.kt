package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.WeatherGlassTypography
import com.weatherglass.app.domain.model.CityLocation

@Composable
fun TopBarSection(
    location: CityLocation,
    currentDateTimeFormatted: String,
    pageCount: Int,
    currentPage: Int,
    onSearchClick: () -> Unit,
    onManageCitiesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Action row: List, Page Dots, Search & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onManageCitiesClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ViewList,
                    contentDescription = "Manage Favourite Cities",
                    tint = GlassColors.TextPrimary
                )
            }

            // City Pager Dots
            if (pageCount > 1) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pageCount) { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 7.dp else 5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) GlassColors.TextPrimary else GlassColors.TextMuted
                                )
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Locations",
                        tint = GlassColors.TextPrimary
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = GlassColors.TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // City & Location indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (location.isCurrentLocation) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Current Location",
                    tint = GlassColors.AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = location.name,
                style = WeatherGlassTypography.headlineMedium,
                color = GlassColors.TextPrimary
            )
        }

        // Region / Country & Date
        val subtitle = if (location.country.isNotBlank()) {
            listOf(location.region, location.country).filter { it.isNotBlank() }.joinToString(", ")
        } else ""

        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = WeatherGlassTypography.bodyMedium,
                color = GlassColors.TextSecondary
            )
        }

        Text(
            text = currentDateTimeFormatted,
            style = WeatherGlassTypography.labelSmall,
            color = GlassColors.TextTertiary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
