package com.weatherglass.app.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.weatherglass.app.core.common.DispatcherProvider
import com.weatherglass.app.domain.model.CityLocation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationProvider(
    private val context: Context,
    private val dispatchers: DispatcherProvider
) {
    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): CityLocation? = withContext(dispatchers.io) {
        if (!hasLocationPermission()) return@withContext null

        val androidLocation = suspendCancellableCoroutine<android.location.Location?> { continuation ->
            val cts = CancellationTokenSource()
            continuation.invokeOnCancellation { cts.cancel() }

            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    if (continuation.isActive) continuation.resume(loc)
                }
                .addOnFailureListener {
                    // Try last location fallback
                    fusedClient.lastLocation
                        .addOnSuccessListener { lastLoc -> if (continuation.isActive) continuation.resume(lastLoc) }
                        .addOnFailureListener { if (continuation.isActive) continuation.resume(null) }
                }
        } ?: return@withContext null

        val lat = androidLocation.latitude
        val lon = androidLocation.longitude

        val (cityName, regionName, countryName) = reverseGeocode(lat, lon)

        CityLocation(
            id = "current_gps_location",
            name = cityName.ifBlank { "Current Location" },
            region = regionName,
            country = countryName,
            latitude = lat,
            longitude = lon,
            timezone = "auto",
            isCurrentLocation = true
        )
    }

    @Suppress("DEPRECATION")
    private fun reverseGeocode(lat: Double, lon: Double): Triple<String, String, String> {
        return runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val addr = addresses?.firstOrNull()
            val city = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea ?: "My Location"
            val region = addr?.adminArea ?: ""
            val country = addr?.countryName ?: ""
            Triple(city, region, country)
        }.getOrDefault(Triple("Current Location", "", ""))
    }
}
