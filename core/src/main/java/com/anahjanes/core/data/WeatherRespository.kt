package com.anahjanes.core.data

import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.core.data.remote.dto.GeoCityDto
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun observeSelectedCity(): Flow<SelectedCity?>

    suspend fun setSelectedCity(city: SelectedCity)

    suspend fun clearSelectedCity()

    suspend fun getWeek(): AppResult<Map<String, List<ForecastItem>>>

    suspend fun getTodayByCoords(lat: Double, lon: Double): AppResult<CurrentWeatherDto>

    suspend fun searchCities(query: String, limit: Int = 5): AppResult<List<GeoCityDto>>
}