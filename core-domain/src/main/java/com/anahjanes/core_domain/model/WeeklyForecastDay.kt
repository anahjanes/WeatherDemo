package com.anahjanes.core_domain.model


data class WeeklyForecastDay(
    val dateKey: String,
    val minTempC: Double,
    val maxTempC: Double,
    val conditionDescription: String?,
    val iconCode: String?,
)