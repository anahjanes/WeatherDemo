package com.anahjanes.core_domain.repository

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CityResult
import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.model.WeeklyForecastDay
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun observeSelectedCity(): Flow<SelectedCity?>

    suspend fun getSelectedCity(): SelectedCity?

    suspend fun clearSelectedCity()

    suspend fun getWeekByCoords(lat: Double, lon: Double): AppResult<List<WeeklyForecastDay>>

    suspend fun getTodayByCoords(
        lat: Double,
        lon: Double,
    ): AppResult<CurrentWeather>

    suspend fun searchCities(query: String, limit: Int = 5): AppResult<List<CityResult>>

    suspend fun saveCity(city: SelectedCity)
}