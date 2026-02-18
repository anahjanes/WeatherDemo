package com.anahjanes.core.data.mapper


import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core_domain.model.CurrentWeather

internal fun CurrentWeatherDto.toModel(): CurrentWeather {
    val w = weather.firstOrNull()

    return CurrentWeather(
        cityName = name,
        timestampSeconds = dt,
        temperatureC = main.temp,
        feelsLikeC = main.feels_like,
        tempMinC = main.temp_min,
        tempMaxC = main.temp_max,
        conditionDescription = w?.description,
        iconCode = w?.icon,
        humidityPct = main.humidity,
        windSpeedMs = wind.speed,
        cloudsPct = clouds?.all ?: 0
    )
}