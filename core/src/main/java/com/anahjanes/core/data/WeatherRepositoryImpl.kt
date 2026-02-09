package com.anahjanes.core.data

import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.dto.GeoCityDto
import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.core.data.remote.WeatherApi
import com.anahjanes.core.data.remote.dto.dayKey
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

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val cityPreferences: CityPreferencesDataSource,
) : WeatherRepository {


    override fun observeSelectedCity(): Flow<SelectedCity?> =
        cityPreferences.selectedCityFlow


    override suspend fun clearSelectedCity() {
        cityPreferences.clear()
    }

    private suspend fun requireSelectedCity(): SelectedCity =
        cityPreferences.selectedCityFlow.first()
            ?: throw IllegalStateException("No hay ciudad seleccionada")


    override suspend fun getWeek(): AppResult<Map<String, List<ForecastItem>>> =
        safeCall {
            val city = requireSelectedCity()
            val response = api.getSevenDayForecastByCoords(city.lat, city.lon)

            response.list
                .filter { it.weather.isNotEmpty() }
                .groupBy { it.dayKey() }
                .toSortedMap()
                .entries
                .take(7)
                .associate { it.toPair() }
        }


    override suspend fun getTodayByCoords(lat: Double, lon: Double): AppResult<CurrentWeatherDto> =
        safeCall {

            val city = cityPreferences.selectedCityFlow.first()?.name?: ""

            val today = api.getCurrentWeatherByCoords(lat = lat, lon = lon, city = city)


            cityPreferences.saveCity(
                SelectedCity(
                    name = today.name,
                    lat = lat,
                    lon = lon
                )
            )

            today
        }

    override suspend fun saveCity(city: SelectedCity) {
        cityPreferences.saveCity(
            city
        )
    }


    override suspend fun searchCities(query: String, limit: Int): AppResult<List<GeoCityDto>> =
        safeCall { api.searchCities(query = query, limit = limit) }
}