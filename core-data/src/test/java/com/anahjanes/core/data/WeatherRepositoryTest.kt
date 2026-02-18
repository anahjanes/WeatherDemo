package com.anahjanes.core.data

import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core.data.remote.WeatherApi
import com.anahjanes.core.data.remote.WeatherApiConfig
import com.anahjanes.core.data.remote.dto.CloudsDto
import com.anahjanes.core.data.remote.dto.CoordDto
import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.dto.MainWeatherDto
import com.anahjanes.core.data.remote.dto.WeatherDescriptionDto
import com.anahjanes.core.data.remote.dto.WindDto
import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core_domain.model.SelectedCity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private val api: WeatherApi = mock()
    private val cityPreferences: CityPreferencesDataSource = mock()

    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        repository = WeatherRepositoryImpl(api, cityPreferences)
    }

    // ----------------------------
    // SUCCESS CASE
    // ----------------------------
    @Test
    fun `getTodayByCoords returns Success when api succeeds`() = runTest {
        val dto = fakeCurrentWeatherDto()

        whenever(api.getCurrentWeatherByCoords(10.0, 20.0))
            .thenReturn(dto)

        val result = repository.getTodayByCoords(10.0, 20.0)

        assertTrue(result is AppResult.Success)
    }

    // ----------------------------
    // IOException -> Network
    // ----------------------------
    @Test
    fun `getTodayByCoords returns Network error when IOException`() = runTest {

        whenever(
            api.getCurrentWeatherByCoords(
                10.0,
                20.0,
                WeatherApiConfig.UNITS,
                WeatherApiConfig.LANG
            )
        ).thenAnswer {
            throw IOException("No internet")
        }

        val result = repository.getTodayByCoords(10.0, 20.0)

        assertTrue(result is AppResult.Error)
        assertEquals(ErrorType.Network, (result as AppResult.Error).type)
    }


    // ----------------------------
    // HttpException -> Http
    // ----------------------------
    @Test
    fun `getTodayByCoords returns Http error when HttpException`() = runTest {

        val response = Response.error<Any>(
            404,
            "Not found".toResponseBody("text/plain".toMediaType())
        )

        whenever(api.getCurrentWeatherByCoords(10.0, 20.0))
            .thenThrow(HttpException(response))

        val result = repository.getTodayByCoords(10.0, 20.0)

        assertTrue(result is AppResult.Error)
        assertEquals(ErrorType.Http, (result as AppResult.Error).type)
    }

    // ----------------------------
    // Unknown error
    // ----------------------------
    @Test
    fun `getTodayByCoords returns Unknown error when unexpected exception`() = runTest {

        whenever(api.getCurrentWeatherByCoords(10.0, 20.0))
            .thenThrow(RuntimeException("Boom"))

        val result = repository.getTodayByCoords(10.0, 20.0)

        assertTrue(result is AppResult.Error)
        assertEquals(ErrorType.Unknown, (result as AppResult.Error).type)
    }

    // ----------------------------
    // observeSelectedCity
    // ----------------------------
    @Test
    fun `observeSelectedCity emits values from data source`() = runTest {

        val city = SelectedCity("Madrid", 40.0, -3.0)
        whenever(cityPreferences.selectedCityFlow)
            .thenReturn(flowOf(city))

        val result = repository.observeSelectedCity().first()

        assertEquals(city, result)
    }

    // ----------------------------
    // saveCity
    // ----------------------------
    @Test
    fun `saveCity calls data source`() = runTest {

        val city = SelectedCity("Madrid", 40.0, -3.0)

        repository.saveCity(city)

        verify(cityPreferences).saveCity(city)
    }
}
private fun fakeCurrentWeatherDto() = CurrentWeatherDto(
    coord = CoordDto(lon = 20.0, lat = 10.0),
    weather = listOf(
        WeatherDescriptionDto(
            id = 1,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
    ),
    main = MainWeatherDto(
        temp = 20.0,
        feels_like = 19.0,
        temp_min = 18.0,
        temp_max = 22.0,
        humidity = 50
    ),
    wind = WindDto(speed = 5.0),
    name = "Madrid",
    dt = 123456789,
    clouds = CloudsDto(0)
)
