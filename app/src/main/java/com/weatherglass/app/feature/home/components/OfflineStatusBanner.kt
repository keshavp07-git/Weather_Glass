package com.weatherglass.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherglass.app.core.common.DateTimeFormatters
import com.weatherglass.app.core.designsystem.GlassColors
import com.weatherglass.app.core.designsystem.WeatherGlassTypography

@Composable
fun OfflineStatusBanner(
    lastUpdatedEpochSeconds: Long,
    isCached: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isCached) return

    val relativeTime = DateTimeFormatters.formatRelativeUpdated(lastUpdatedEpochSeconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x33F59E0B)) // Amber translucent
            .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Offline Cache • Updated $relativeTime",
                style = WeatherGlassTypography.labelSmall.copy(fontSize = 12.sp),
                color = GlassColors.TextPrimary
            )
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry Sync",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
