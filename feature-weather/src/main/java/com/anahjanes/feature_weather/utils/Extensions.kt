package com.anahjanes.feature_weather.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun String.toDayOfWeek(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = format.parse(this) ?: return ""

    val calendar = Calendar.getInstance().apply {
        time = date
    }

    return calendar.getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.LONG,
        Locale.ENGLISH
    ) ?: ""
}

fun String.toWeatherIconUrl(size: Int = 4): String =
    "https://openweathermap.org/img/wn/${this}@${size}x.png"

fun String.toShortDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(this) ?: return this

        val outputFormat = SimpleDateFormat("d MMM", Locale.US)
        outputFormat.format(date)

    } catch (e: Exception) {
        this
    }
}