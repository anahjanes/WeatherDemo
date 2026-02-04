package com.anahjanes.core.data

import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.CurrentWeatherDto
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.GeoCityDto
import com.anahjanes.core.data.remote.OneCallDto
import com.anahjanes.core.data.remote.WeatherApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


private inline fun <T> safeCall(block: () -> T): AppResult<T> =
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
    private val cityPreferences: CityPreferencesDataSource
) : WeatherRepository {


    override fun observeSelectedCity(): Flow<SelectedCity?> =
        cityPreferences.selectedCityFlow

    override suspend fun setSelectedCity(city: SelectedCity) {
        cityPreferences.saveCity(city)
    }

    override suspend fun clearSelectedCity() {
        cityPreferences.clear()
    }

    private suspend fun requireSelectedCity(): SelectedCity =
        cityPreferences.selectedCityFlow.first()
            ?: throw IllegalStateException("No hay ciudad seleccionada")


    override suspend fun getToday(): AppResult<CurrentWeatherDto> =
        safeCall {
            val city = requireSelectedCity()
            api.getCurrentWeatherByCoords(lat = city.lat, lon = city.lon)
        }

    override suspend fun getWeek(): AppResult<OneCallDto> =
        safeCall {
            val city = requireSelectedCity()
            api.getWeeklyForecastByCoords(lat = city.lat, lon = city.lon)
        }



    override suspend fun getTodayByCity(cityName: String): AppResult<CurrentWeatherDto> =
        safeCall {
            val today = api.getCurrentWeatherByCity(city = cityName)

            cityPreferences.saveCity(
                SelectedCity(
                    name = today.name,
                    lat = today.coord.lat,
                    lon = today.coord.lon
                )
            )

            today
        }

    override suspend fun getTodayByCoords(lat: Double, lon: Double): AppResult<CurrentWeatherDto> =
        safeCall {
            val today = api.getCurrentWeatherByCoords(lat = lat, lon = lon)

            cityPreferences.saveCity(
                SelectedCity(
                    name = today.name,
                    lat = lat,
                    lon = lon
                )
            )

            today
        }

    override suspend fun getWeekByCoords(lat: Double, lon: Double): AppResult<OneCallDto> =
        safeCall {
            api.getWeeklyForecastByCoords(lat = lat, lon = lon)
        }

    // ───────────── Search ─────────────

    override suspend fun searchCities(query: String, limit: Int): AppResult<List<GeoCityDto>> =
        safeCall { api.searchCities(query = query, limit = limit) }
}