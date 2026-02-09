package com.anahjanes.feature_weather.week.model

data class WeekUiModel(
    val day: String,
    val date: String? = null,
    val iconUrl: String?,
    val weather: String,
    val maxTemp: Int,
    val minTemp: Int
)
