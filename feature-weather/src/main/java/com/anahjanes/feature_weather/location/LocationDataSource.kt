package com.anahjanes.feature_weather.location

data class LatLon(val lat: Double, val lon: Double)

interface LocationDataSource {
    suspend fun getCurrentLocation(): Result<LatLon>
    fun hasLocationPermission(): Boolean
}