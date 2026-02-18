package com.anahjanes.core.data.mapper

import com.anahjanes.core.data.remote.dto.WeekWeatherDto
import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.core.data.remote.dto.dayKey
import com.anahjanes.core_domain.model.WeeklyForecastDay

internal fun WeekWeatherDto.toModel(): List<WeeklyForecastDay> {
    return list
        .asSequence()
        .filter { it.weather.isNotEmpty() }
        .groupBy { it.dayKey() }
        .toSortedMap()
        .entries
        .take(7)
        .map { (dateKey, items) ->
            items.toWeeklyForecastDay(dateKey)
        }
        .toList()
}

internal fun List<ForecastItem>.toWeeklyForecastDay(dateKey: String): WeeklyForecastDay {
    val representative = representativeItem()

    val minTemp = minOf { it.main.temp_min }
    val maxTemp = maxOf { it.main.temp_max }

    val w = representative.weather.firstOrNull()

    return WeeklyForecastDay(
        dateKey = dateKey,
        minTempC = minTemp,
        maxTempC = maxTemp,
        conditionDescription = w?.description,
        iconCode = w?.icon
    )
}

private fun List<ForecastItem>.representativeItem(): ForecastItem {
    return firstOrNull { it.dt_txt.contains("12:00:00") } ?: first()
}