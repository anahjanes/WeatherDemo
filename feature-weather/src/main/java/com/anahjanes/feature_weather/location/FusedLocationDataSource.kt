package com.anahjanes.feature_weather.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class FusedLocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationDataSource {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<LatLon> = runCatching {
        val location = client.lastLocation.await()
            ?: throw IllegalStateException("Location not available")

        LatLon(
            lat = location.latitude,
            lon = location.longitude
        )
    }

    override fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }
}