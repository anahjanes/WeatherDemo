package com.anahjanes.core.data.remote.dto


data class GeoCityDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)