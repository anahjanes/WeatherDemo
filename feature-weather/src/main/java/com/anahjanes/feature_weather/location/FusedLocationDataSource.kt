package com.anahjanes.feature_weather.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class FusedLocationDataSource @Inject constructor(
    @ApplicationContext context: Context
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
}