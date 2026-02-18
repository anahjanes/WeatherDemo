package com.anahjanes.feature_weather.home.model


import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.feature_weather.utils.toWeatherIconUrl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


fun CurrentWeather.toHomeUiModel(): HomeUiModel {
    val iconUrl = iconCode?.toWeatherIconUrl()

    val dateText = SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH)
        .format(Date(timestampSeconds * 1000))

    val temp = temperatureC.roundToInt()
    val feels = feelsLikeC.roundToInt()
    val max = tempMaxC.roundToInt()
    val min = tempMinC.roundToInt()

    val windKmh = (windSpeedMs * 3.6).roundToInt()

    return HomeUiModel(
        city = cityName,
        dateText = dateText,
        temperature = "${temp}°",
        condition = conditionDescription?.replaceFirstChar { it.uppercase() } ?: "-",
        feelsLike = "${feels}°C",
        iconUrl = iconUrl,
        tempMax = "${max}°",
        tempMin = "${min}°",
        clouds = "${cloudsPct}%",
        wind = "$windKmh km/h",
        humidity = "${humidityPct}%"
    )
}