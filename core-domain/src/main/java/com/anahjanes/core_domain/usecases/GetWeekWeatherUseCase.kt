package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.WeeklyForecastDay
import com.anahjanes.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeekWeatherUseCase @Inject constructor(
    private val repo: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): AppResult<List<WeeklyForecastDay>> =
        repo.getWeekByCoords(lat, lon)
}