package com.anahjanes.core.data.remote.dto

internal data class WeekWeatherDto(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<ForecastItem>,
    val message: Int
)

internal data class ForecastItem(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: Main,
    val pop: Double,
    val rain: Rain,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)
internal fun ForecastItem.dayKey(): String = dt_txt.substring(0, 10)

internal data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
)

internal data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

internal data class Coord(
    val lat: Double,
    val lon: Double
)

internal data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Double,
    val temp_max: Double,
    val temp_min: Double
)

internal data class Sys(
    val pod: String
)
internal data class Rain(
    val `3h`: Double
)
internal data class Clouds(
    val all: Int
)

internal data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)