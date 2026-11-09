package com.weatherglass.app.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weatherglass.app.domain.model.WeatherCondition
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherConditionIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (condition) {
            WeatherCondition.CLEAR_DAY -> drawSun(w, h)
            WeatherCondition.CLEAR_NIGHT -> drawMoon(w, h)
            WeatherCondition.PARTLY_CLOUDY_DAY -> {
                drawSunBehindCloud(w, h)
                drawCloud(w, h, scale = 0.85f, offsetY = 0.15f)
            }
            WeatherCondition.PARTLY_CLOUDY_NIGHT -> {
                drawMoon(w, h, scale = 0.75f, offsetX = -0.15f)
                drawCloud(w, h, scale = 0.85f, offsetY = 0.15f)
            }
            WeatherCondition.OVERCAST -> drawCloud(w, h, isDark = true)
            WeatherCondition.FOG -> drawFog(w, h)
            WeatherCondition.LIGHT_DRIZZLE -> {
                drawCloud(w, h, scale = 0.85f)
                drawRainDrops(w, h, count = 2)
            }
            WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> {
                drawCloud(w, h, isDark = true, scale = 0.85f)
                drawRainDrops(w, h, count = 4)
            }
            WeatherCondition.THUNDERSTORM -> {
                drawCloud(w, h, isDark = true, scale = 0.85f)
                drawLightning(w, h)
            }
            WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW -> {
                drawCloud(w, h, scale = 0.85f)
                drawSnowDots(w, h)
            }
            WeatherCondition.WINDY -> drawWindLines(w, h)
        }
    }
}

private fun DrawScope.drawSun(w: Float, h: Float) {
    val center = Offset(w * 0.5f, h * 0.5f)
    val radius = w * 0.28f

    // Corona rays
    for (i in 0 until 8) {
        val angle = i * (Math.PI / 4)
        val startX = center.x + (radius * 1.25f * cos(angle)).toFloat()
        val startY = center.y + (radius * 1.25f * sin(angle)).toFloat()
        val endX = center.x + (radius * 1.55f * cos(angle)).toFloat()
        val endY = center.y + (radius * 1.55f * sin(angle)).toFloat()
        drawLine(
            color = Color(0xFFFFD166),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawMoon(w: Float, h: Float, scale: Float = 1f, offsetX: Float = 0f) {
    val cx = w * (0.5f + offsetX)
    val cy = h * 0.5f
    val radius = w * 0.32f * scale

    val moonPath = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(cx - radius, cy - radius, cx + radius, cy + radius))
    }

    drawPath(
        path = moonPath,
        color = Color(0xFFF1F5F9)
    )

    // Inner shadow creates crescent
    drawCircle(
        color = Color(0xFF1E293B),
        radius = radius * 0.85f,
        center = Offset(cx + radius * 0.42f, cy - radius * 0.2f)
    )
}

private fun DrawScope.drawSunBehindCloud(w: Float, h: Float) {
    val center = Offset(w * 0.7f, h * 0.35f)
    val radius = w * 0.2f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFD166), Color(0xFFFF9800)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawCloud(w: Float, h: Float, isDark: Boolean = false, scale: Float = 1f, offsetY: Float = 0f) {
    val cloudColor = if (isDark) Color(0xFF94A3B8) else Color(0xFFE2E8F0)
    val cy = h * (0.55f + offsetY)

    drawRoundRect(
        color = cloudColor,
        topLeft = Offset(w * 0.15f, cy - h * 0.12f),
        size = Size(w * 0.7f * scale, h * 0.32f * scale),
        cornerRadius = CornerRadius(h * 0.16f, h * 0.16f)
    )
    drawCircle(
        color = cloudColor,
        radius = w * 0.22f * scale,
        center = Offset(w * 0.42f, cy - h * 0.1f)
    )
    drawCircle(
        color = cloudColor,
        radius = w * 0.16f * scale,
        center = Offset(w * 0.65f, cy - h * 0.04f)
    )
}

private fun DrawScope.drawRainDrops(w: Float, h: Float, count: Int) {
    val dropColor = Color(0xFF38BDF8)
    val spacing = w * 0.7f / (count + 1)
    for (i in 1..count) {
        val x = w * 0.15f + spacing * i
        drawLine(
            color = dropColor,
            start = Offset(x, h * 0.75f),
            end = Offset(x - w * 0.04f, h * 0.92f),
            strokeWidth = w * 0.06f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawLightning(w: Float, h: Float) {
    val path = Path().apply {
        moveTo(w * 0.52f, h * 0.65f)
        lineTo(w * 0.42f, h * 0.8f)
        lineTo(w * 0.52f, h * 0.8f)
        lineTo(w * 0.46f, h * 0.98f)
    }
    drawPath(
        path = path,
        color = Color(0xFFFFD166),
        style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawSnowDots(w: Float, h: Float) {
    val snowColor = Color.White
    drawCircle(snowColor, radius = w * 0.05f, center = Offset(w * 0.35f, h * 0.82f))
    drawCircle(snowColor, radius = w * 0.06f, center = Offset(w * 0.55f, h * 0.88f))
    drawCircle(snowColor, radius = w * 0.05f, center = Offset(w * 0.72f, h * 0.8f))
}

private fun DrawScope.drawFog(w: Float, h: Float) {
    val fogColor = Color(0xCCE2E8F0)
    for (i in 0..2) {
        val y = h * (0.35f + i * 0.2f)
        drawLine(
            color = fogColor,
            start = Offset(w * 0.15f, y),
            end = Offset(w * 0.85f, y),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawWindLines(w: Float, h: Float) {
    val windColor = Color(0xFF94A3B8)
    drawLine(
        color = windColor,
        start = Offset(w * 0.1f, h * 0.4f),
        end = Offset(w * 0.75f, h * 0.4f),
        strokeWidth = w * 0.07f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = windColor,
        start = Offset(w * 0.25f, h * 0.6f),
        end = Offset(w * 0.9f, h * 0.6f),
        strokeWidth = w * 0.07f,
        cap = StrokeCap.Round
    )
}
