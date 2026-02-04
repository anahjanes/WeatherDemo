package com.anahjanes.core.data.remote

data class OneCallDto(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val daily: List<DailyForecastDto>
)
data class DailyForecastDto(
    val dt: Long,
    val temp: DailyTempDto,
    val weather: List<WeatherDescriptionDto>
)
data class DailyTempDto(
    val min: Double,
    val max: Double,
    val day: Double
)