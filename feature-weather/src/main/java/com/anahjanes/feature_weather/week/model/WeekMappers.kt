package com.anahjanes.feature_weather.week.model

import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.feature_weather.utils.toDayOfWeek
import com.anahjanes.feature_weather.utils.toShortDate
import com.anahjanes.feature_weather.utils.toWeatherIconUrl

fun Map<String, List<ForecastItem>>.toWeeklyForecast(): List<WeekUiModel> {
    return entries.map { (dateKey, items) ->

        // Item representativo: el de las 12:00 si existe
        val representative = items.firstOrNull {
            it.dt_txt.contains("12:00:00")
        } ?: items.first()

        val minTemp = items.minOf { it.main.temp_min }.toInt()
        val maxTemp = items.maxOf { it.main.temp_max }.toInt()

        WeekUiModel(
            day = dateKey.toDayOfWeek(),
            date = dateKey.toShortDate(),
            iconUrl = representative.weather.firstOrNull()?.icon?.toWeatherIconUrl(),
            weather = representative.weather.firstOrNull()?.description.orEmpty(),
            maxTemp = maxTemp,
            minTemp = minTemp
        )
    }
}

