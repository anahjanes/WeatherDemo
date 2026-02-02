package com.anahjanes.core.data

import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core.data.mapper.toModel
import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core.data.remote.WeatherApi
import com.anahjanes.core_domain.model.CityResult
import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.core_domain.model.WeeklyForecastDay
import com.anahjanes.core_domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private suspend fun <T> safeCall(block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: IOException) {
        AppResult.Error(ErrorType.Network, e.message, e)
    } catch (e: HttpException) {
        AppResult.Error(ErrorType.Http, "HTTP ${e.code()}", e)
    } catch (e: Throwable) {
        AppResult.Error(ErrorType.Unknown, e.message, e)
    }

internal class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val cityPreferences: CityPreferencesDataSource,
) : WeatherRepository {

    override fun observeSelectedCity(): Flow<SelectedCity?> =
        cityPreferences.selectedCityFlow

    override suspend fun getSelectedCity(): SelectedCity? =
        cityPreferences.selectedCityFlow.first()

    override suspend fun clearSelectedCity() {
        cityPreferences.clear()
    }

    override suspend fun getWeekByCoords(lat: Double, lon: Double): AppResult<List<WeeklyForecastDay>> =
        safeCall {
            api.getSevenDayForecastByCoords(lat, lon).toModel()
        }

    override suspend fun getTodayByCoords(
        lat: Double,
        lon: Double
    ): AppResult<CurrentWeather> =
        safeCall {
            api.getCurrentWeatherByCoords(lat = lat, lon = lon, ).toModel()
        }

    override suspend fun searchCities(query: String, limit: Int): AppResult<List<CityResult>> =
        safeCall { api.searchCities(query = query, limit = limit).map { it.toModel() } }

    override suspend fun saveCity(city: SelectedCity) {
        cityPreferences.saveCity(city)
    }
}