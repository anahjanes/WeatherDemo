package com.anahjanes.feature_weather.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.anahjanes.core.data.remote.CurrentWeatherDto
import com.anahjanes.feature_weather.model.HomeWeatherUiModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


fun CurrentWeatherDto.toHomeUiModel(): HomeWeatherUiModel {
    val w = weather.firstOrNull()
    val icon = w?.icon?.let { "https://openweathermap.org/img/wn/$it@4x.png" }

    val dateText = SimpleDateFormat(
        "EEEE, d MMM",
        Locale.ENGLISH   // o Locale("es", "ES")
    ).format(Date(dt * 1000))

    val temp = main.temp.roundToInt()
    val feels = main.feels_like.roundToInt()
    val max = main.temp_max.roundToInt()
    val min = main.temp_min.roundToInt()

    val cloudsPct = clouds?.all ?: 0
    val humidityPct = main.humidity
    val windKmh = (wind.speed * 3.6).roundToInt()

    return HomeWeatherUiModel(
        city = name,
        dateText = dateText,
        temperature = "${temp}°",
        condition = w?.description?.replaceFirstChar { it.uppercase() } ?: "-",
        feelsLike = "Feels like: ${feels}°C",
        iconUrl = icon,
        tempMax = "${max}°",
        tempMin = "${min}°",
        clouds = "${cloudsPct}%",
        wind = "$windKmh km/h",
        humidity = "${humidityPct}%"
    )
}