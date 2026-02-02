package com.anahjanes.feature_weather.home.model

data class HomeUiModel(
    val city: String,
    val dateText: String,          // "Monday, 24 May"
    val temperature: String,       // "12°"
    val condition: String,         // "Broken Clouds"
    val feelsLike: String,         // "Feels like: 10°C"
    val iconUrl: String?,          // URL del icono (OpenWeather)
    val tempMax: String,           // "14°"
    val tempMin: String,           // "8°"
    val clouds: String,            // "75%"
    val wind: String,              // "12 km/h"
    val humidity: String           // "64%"
)