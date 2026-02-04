package com.anahjanes.core.data.remote


data class CurrentWeatherDto(
    val coord: CoordDto,
    val weather: List<WeatherDescriptionDto>,
    val main: MainWeatherDto,
    val wind: WindDto,
    val name: String, // nombre de la ciudad
    val dt: Long
)

data class CoordDto(
    val lon: Double,
    val lat: Double
)

data class WeatherDescriptionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainWeatherDto(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class WindDto(
    val speed: Double
)