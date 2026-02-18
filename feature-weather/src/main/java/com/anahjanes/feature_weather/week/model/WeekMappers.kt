package com.anahjanes.feature_weather.week.model

import com.anahjanes.core_domain.model.WeeklyForecastDay
import com.anahjanes.feature_weather.utils.toDayOfWeek
import com.anahjanes.feature_weather.utils.toShortDate
import com.anahjanes.feature_weather.utils.toWeatherIconUrl

fun List<WeeklyForecastDay>.toWeekUi(): List<WeekUiModel> =
    map { day ->
        WeekUiModel(
            day = day.dateKey.toDayOfWeek(),
            date = day.dateKey.toShortDate(),
            iconUrl = day.iconCode?.toWeatherIconUrl(),
            weather = day.conditionDescription.orEmpty(),
            maxTemp = day.maxTempC.toInt(),
            minTemp = day.minTempC.toInt()
        )
    }