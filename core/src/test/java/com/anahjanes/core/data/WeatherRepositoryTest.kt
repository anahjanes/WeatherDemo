package com.anahjanes.core.data


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.CoordDto
import com.anahjanes.core.data.remote.CurrentWeatherDto
import com.anahjanes.core.data.remote.DailyForecastDto
import com.anahjanes.core.data.remote.DailyTempDto
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.MainWeatherDto
import com.anahjanes.core.data.remote.OneCallDto
import com.anahjanes.core.data.remote.WeatherApi
import com.anahjanes.core.data.remote.WeatherDescriptionDto
import com.anahjanes.core.data.remote.WindDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody


import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response


private class FakeCityPreferences : CityPreferencesDataSource {
    private val state = MutableStateFlow<SelectedCity?>(null)
    override val selectedCityFlow: Flow<SelectedCity?> = state

    override suspend fun saveCity(city: SelectedCity) {
        state.value = city
    }

    override suspend fun clear() {
        state.value = null
    }

    fun current(): SelectedCity? = state.value
}

class WeatherRepositoryImplTest {

    private lateinit var api: WeatherApi
    private lateinit var prefs: FakeCityPreferences
    private lateinit var repo: WeatherRepositoryImpl

    @Before
    fun setUp() {
        api = mock()
        prefs = FakeCityPreferences()
        repo = WeatherRepositoryImpl(api = api, cityPreferences = prefs)
    }

    @Test
    fun `getTodayByCity calls api and saves selected city from response`() = runTest {
        val dto = CurrentWeatherDto(
            coord = CoordDto(lon = 2.17, lat = 41.38),
            weather = listOf(WeatherDescriptionDto(800, "Clear", "clear sky", "01d")),
            main = MainWeatherDto(20.0, 19.0, 18.0, 22.0, 50),
            wind = WindDto(3.5),
            name = "Barcelona",
            dt = 1234567890L
        )

        whenever(api.getCurrentWeatherByCity(city = "Barcelona", units = "metric", lang = "es"))
            .thenReturn(dto)

        val result = repo.getTodayByCity("Barcelona")

        assertTrue(result is AppResult.Success)
        assertEquals("Barcelona", (result as AppResult.Success).data.name)

        verify(api).getCurrentWeatherByCity(city = "Barcelona", units = "metric", lang = "es")

        val saved = prefs.current()
        assertNotNull(saved)
        assertEquals("Barcelona", saved!!.name)
        assertEquals(41.38, saved.lat, 0.0001)
        assertEquals(2.17, saved.lon, 0.0001)
    }

    @Test
    fun `getToday uses stored coords and calls current weather by coords`() = runTest {
        prefs.saveCity(SelectedCity(name = "SavedCity", lat = 10.0, lon = 20.0))

        val dto = CurrentWeatherDto(
            coord = CoordDto(lon = 20.0, lat = 10.0),
            weather = listOf(WeatherDescriptionDto(801, "Clouds", "few clouds", "02d")),
            main = MainWeatherDto(15.0, 15.0, 14.0, 16.0, 60),
            wind = WindDto(2.0),
            name = "SavedCity",
            dt = 111L
        )

        whenever(api.getCurrentWeatherByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "es"))
            .thenReturn(dto)

        val result = repo.getToday()

        assertTrue(result is AppResult.Success)
        verify(api).getCurrentWeatherByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "es")
    }

    @Test
    fun `getWeek uses stored coords and calls onecall by coords`() = runTest {
        prefs.saveCity(SelectedCity(name = "SavedCity", lat = 41.38, lon = 2.17))

        val dto = OneCallDto(
            lat = 41.38,
            lon = 2.17,
            timezone = "Europe/Madrid",
            daily = listOf(
                DailyForecastDto(
                    dt = 123L,
                    temp = DailyTempDto(min = 10.0, max = 18.0, day = 15.0),
                    weather = listOf(WeatherDescriptionDto(500, "Rain", "light rain", "10d"))
                )
            )
        )

        whenever(
            api.getWeeklyForecastByCoords(
                lat = 41.38,
                lon = 2.17,
                exclude = "minutely,hourly,alerts",
                units = "metric",
                lang = "es"
            )
        ).thenReturn(dto)

        val result = repo.getWeek()

        assertTrue(result is AppResult.Success)
        verify(api).getWeeklyForecastByCoords(
            lat = 41.38,
            lon = 2.17,
            exclude = "minutely,hourly,alerts",
            units = "metric",
            lang = "es"
        )
    }

    @Test
    fun `getToday returns Error when no selected city`() = runTest {
        val result = repo.getToday()
        assertTrue(result is AppResult.Error)
        val err = result as AppResult.Error
        assertEquals(ErrorType.Unknown, err.type) // IllegalStateException -> Unknown
    }

    @Test
    fun `getTodayByCity returns Http error on HttpException`() = runTest {
        val errorBody = """{"message":"Unauthorized"}"""
            .toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(401, errorBody)
        val httpException = HttpException(response)

        whenever(api.getCurrentWeatherByCity(city = "X", units = "metric", lang = "es"))
            .thenThrow(httpException)

        val result = repo.getTodayByCity("X")

        assertTrue(result is AppResult.Error)
        val err = result as AppResult.Error
        assertEquals(ErrorType.Http, err.type)
    }

    @Test
    fun `getTodayByCity returns Unknown error on RuntimeException`() = runTest {
        whenever(api.getCurrentWeatherByCity(city = "Y", units = "metric", lang = "es"))
            .thenThrow(RuntimeException("boom"))

        val result = repo.getTodayByCity("Y")

        assertTrue(result is AppResult.Error)
        val err = result as AppResult.Error
        assertEquals(ErrorType.Unknown, err.type)
    }

    @Test
    fun `getTodayByCoords calls api and saves selected city`() = runTest {
        val dto = CurrentWeatherDto(
            coord = CoordDto(lon = 2.17, lat = 41.38),
            weather = listOf(WeatherDescriptionDto(800, "Clear", "clear sky", "01d")),
            main = MainWeatherDto(20.0, 19.0, 18.0, 22.0, 50),
            wind = WindDto(3.5),
            name = "Barcelona",
            dt = 1234567890L
        )

        whenever(api.getCurrentWeatherByCoords(lat = 41.38, lon = 2.17, units = "metric", lang = "es"))
            .thenReturn(dto)

        val result = repo.getTodayByCoords(41.38, 2.17)

        assertTrue(result is AppResult.Success)
        verify(api).getCurrentWeatherByCoords(lat = 41.38, lon = 2.17, units = "metric", lang = "es")

        val saved = prefs.current()
        assertNotNull(saved)
        assertEquals("Barcelona", saved!!.name)
        assertEquals(41.38, saved.lat, 0.0001)
        assertEquals(2.17, saved.lon, 0.0001)
    }
}