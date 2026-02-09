package com.anahjanes.feature_weather.home.model

import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.feature_weather.utils.toWeatherIconUrl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


fun CurrentWeatherDto.toHomeUiModel(): HomeUiModel {
    val w = weather.firstOrNull()
    val icon = w?.icon?.toWeatherIconUrl()

    val dateText = SimpleDateFormat(
        "EEEE, d MMM",
        Locale.ENGLISH
    ).format(Date(dt * 1000))

    val temp = main.temp.roundToInt()
    val feels = main.feels_like.roundToInt()
    val max = main.temp_max.roundToInt()
    val min = main.temp_min.roundToInt()

    val cloudsPct = clouds?.all ?: 0
    val humidityPct = main.humidity
    val windKmh = (wind.speed * 3.6).roundToInt()

    return HomeUiModel(
        city = name,
        dateText = dateText,
        temperature = "${temp}°",
        condition = w?.description?.replaceFirstChar { it.uppercase() } ?: "-",
        feelsLike = "${feels}°C",
        iconUrl = icon,
        tempMax = "${max}°",
        tempMin = "${min}°",
        clouds = "${cloudsPct}%",
        wind = "$windKmh km/h",
        humidity = "${humidityPct}%"
    )
}