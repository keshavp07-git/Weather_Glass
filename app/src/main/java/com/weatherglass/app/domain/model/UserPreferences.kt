package com.weatherglass.app.domain.model

enum class TemperatureUnit(val symbol: String, val displayName: String) {
    CELSIUS("°C", "Celsius (°C)"),
    FAHRENHEIT("°F", "Fahrenheit (°F)")
}

enum class WindSpeedUnit(val symbol: String, val displayName: String) {
    KMH("km/h", "Kilometers per hour (km/h)"),
    MS("m/s", "Meters per second (m/s)"),
    MPH("mph", "Miles per hour (mph)"),
    KNOTS("kn", "Knots (kn)")
}

enum class PressureUnit(val symbol: String, val displayName: String) {
    HPA("hPa", "Hectopascals (hPa)"),
    MMHG("mmHg", "Millimeters of mercury (mmHg)"),
    INHG("inHg", "Inches of mercury (inHg)")
}

enum class PrecipitationUnit(val symbol: String, val displayName: String) {
    MILLIMETERS("mm", "Millimeters (mm)"),
    INCHES("in", "Inches (in)")
}

enum class VisibilityUnit(val symbol: String, val displayName: String) {
    KILOMETERS("km", "Kilometers (km)"),
    MILES("mi", "Miles (mi)")
}

enum class TimeFormat(val displayName: String) {
    TWELVE_HOUR("12-Hour (e.g. 3:00 PM)"),
    TWENTY_FOUR_HOUR("24-Hour (e.g. 15:00)")
}

enum class AppTheme(val displayName: String) {
    SYSTEM("Follow System"),
    DARK("Atmospheric Dark (Default)"),
    LIGHT("Daylight Pure")
}

data class UserPreferences(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KMH,
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.MILLIMETERS,
    val visibilityUnit: VisibilityUnit = VisibilityUnit.KILOMETERS,
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    val theme: AppTheme = AppTheme.DARK,
    val animationsEnabled: Boolean = true,
    val selectedProviderId: String = "open_meteo",
    val autoSyncEnabled: Boolean = true,
    val autoRefreshMinutes: Int = 30
)
