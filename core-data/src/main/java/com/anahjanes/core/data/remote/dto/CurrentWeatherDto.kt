package com.anahjanes.core.data.remote.dto


internal data class CurrentWeatherDto(
    val coord: CoordDto,
    val weather: List<WeatherDescriptionDto>,
    val main: MainWeatherDto,
    val wind: WindDto,
    val name: String,
    val dt: Long,
    val clouds: CloudsDto?
)
internal data class CloudsDto(val all: Int)


internal data class CoordDto(
    val lon: Double,
    val lat: Double
)

internal data class WeatherDescriptionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

internal data class MainWeatherDto(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

internal data class WindDto(
    val speed: Double
)