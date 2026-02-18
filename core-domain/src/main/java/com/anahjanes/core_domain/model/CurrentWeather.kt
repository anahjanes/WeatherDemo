package com.anahjanes.core_domain.model

data class CurrentWeather(
    val cityName: String,
    val timestampSeconds: Long,

    val temperatureC: Double,
    val feelsLikeC: Double,
    val tempMinC: Double,
    val tempMaxC: Double,

    val conditionDescription: String?, // "clear sky", etc.
    val iconCode: String?,             // "01d"
    val humidityPct: Int,
    val windSpeedMs: Double,
    val cloudsPct: Int
)