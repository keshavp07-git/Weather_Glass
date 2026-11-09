package com.weatherglass.app.core.common

import com.weatherglass.app.domain.model.TimeFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeFormatters {

    fun formatHour(epochSeconds: Long, timeFormat: TimeFormat, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val instant = Instant.ofEpochSecond(epochSeconds)
        val pattern = if (timeFormat == TimeFormat.TWELVE_HOUR) "h a" else "HH:mm"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US).withZone(zoneId)
        return formatter.format(instant)
    }

    fun formatDayOfWeek(epochSeconds: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val instant = Instant.ofEpochSecond(epochSeconds)
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.US).withZone(zoneId)
        return formatter.format(instant)
    }

    fun formatFullDate(epochSeconds: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val instant = Instant.ofEpochSecond(epochSeconds)
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.US).withZone(zoneId)
        return formatter.format(instant)
    }

    fun formatTime(epochSeconds: Long, timeFormat: TimeFormat, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val instant = Instant.ofEpochSecond(epochSeconds)
        val pattern = if (timeFormat == TimeFormat.TWELVE_HOUR) "h:mm a" else "HH:mm"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US).withZone(zoneId)
        return formatter.format(instant)
    }

    fun formatRelativeUpdated(epochSeconds: Long): String {
        val now = System.currentTimeMillis() / 1000
        val diffMinutes = ((now - epochSeconds) / 60).coerceAtLeast(0)
        return when {
            diffMinutes < 1 -> "Just now"
            diffMinutes == 1L -> "1 min ago"
            diffMinutes < 60 -> "$diffMinutes mins ago"
            else -> {
                val hours = diffMinutes / 60
                if (hours == 1L) "1 hr ago" else "$hours hrs ago"
            }
        }
    }
}
