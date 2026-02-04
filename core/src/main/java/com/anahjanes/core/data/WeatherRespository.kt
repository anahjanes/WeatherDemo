package com.anahjanes.core.data

import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.CurrentWeatherDto
import com.anahjanes.core.data.remote.GeoCityDto
import com.anahjanes.core.data.remote.OneCallDto
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun observeSelectedCity(): Flow<SelectedCity?>
    suspend fun setSelectedCity(city: SelectedCity)
    suspend fun clearSelectedCity()

    suspend fun getToday(): AppResult<CurrentWeatherDto>
    suspend fun getWeek(): AppResult<OneCallDto>

    suspend fun getTodayByCity(cityName: String): AppResult<CurrentWeatherDto>
    suspend fun getTodayByCoords(lat: Double, lon: Double): AppResult<CurrentWeatherDto>
    suspend fun getWeekByCoords(lat: Double, lon: Double): AppResult<OneCallDto>

    suspend fun searchCities(query: String, limit: Int = 5): AppResult<List<GeoCityDto>>
}