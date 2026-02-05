package com.anahjanes.feature_weather.week

import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.feature_weather.week.model.WeekUiModel
import com.anahjanes.feature_weather.utils.toWeatherIconUrl
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            date = dateKey,
            iconUrl = representative.weather.firstOrNull()?.icon?.toWeatherIconUrl(),
            weather = representative.weather.firstOrNull()?.description.orEmpty(),
            maxTemp = maxTemp,
            minTemp = minTemp
        )
    }
}


fun String.toDayOfWeek(): String {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormat.parse(this) ?: return ""

    val calendar = Calendar.getInstance()
    calendar.time = date

    return calendar.getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.SHORT,
        Locale.getDefault()
    ) ?: ""
}