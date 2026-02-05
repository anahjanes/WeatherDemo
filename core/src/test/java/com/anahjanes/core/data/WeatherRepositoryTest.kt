package com.anahjanes.core.data


import com.anahjanes.core.data.local.CityPreferencesDataSource
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.WeatherApi
import com.anahjanes.core.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

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
    private lateinit var repo: WeatherRepository

    @Before
    fun setUp() {
        api = mock()
        prefs = FakeCityPreferences()
        repo = WeatherRepositoryImpl(api = api, cityPreferences = prefs)
    }

    @Test
    fun `observeSelectedCity emits current saved city`() = runTest {
        prefs.saveCity(SelectedCity(name = "Barcelona", lat = 41.38, lon = 2.17))

        val emitted = repo.observeSelectedCity().first()

        assertNotNull(emitted)
        assertEquals("Barcelona", emitted!!.name)
        assertEquals(41.38, emitted.lat, 0.0001)
        assertEquals(2.17, emitted.lon, 0.0001)
        verifyNoInteractions(api)
    }

    @Test
    fun `setSelectedCity saves city`() = runTest {
        repo.setSelectedCity(SelectedCity("Madrid", 40.41, -3.70))
        assertEquals("Madrid", prefs.current()!!.name)
        verifyNoInteractions(api)
    }

    @Test
    fun `clearSelectedCity clears saved city`() = runTest {
        prefs.saveCity(SelectedCity("X", 1.0, 2.0))
        repo.clearSelectedCity()
        assertNull(prefs.current())
        verifyNoInteractions(api)
    }

    @Test
    fun `getTodayByCoords calls api and saves selected city`() = runTest {
        val dto = CurrentWeatherDto(
            coord = CoordDto(lon = 2.17, lat = 41.38),
            weather = listOf(WeatherDescriptionDto(800, "Clear", "clear sky", "01d")),
            main = MainWeatherDto(20.0, 19.0, 18.0, 22.0, 50),
            wind = WindDto(3.5),
            name = "Barcelona",
            dt = 1234567890L,
            clouds = CloudsDto(79)
        )

        whenever(
            api.getCurrentWeatherByCoords(
                lat = 41.38,
                lon = 2.17,
                units = "metric",
                lang = "en"
            )
        ).thenReturn(dto)

        val result = repo.getTodayByCoords(41.38, 2.17)

        assertTrue(result is AppResult.Success)
        verify(api).getCurrentWeatherByCoords(lat = 41.38, lon = 2.17, units = "metric", lang = "en")
        verifyNoMoreInteractions(api)

        val saved = prefs.current()
        assertNotNull(saved)
        assertEquals("Barcelona", saved!!.name)
        assertEquals(41.38, saved.lat, 0.0001)
        assertEquals(2.17, saved.lon, 0.0001)
    }

    @Test
    fun `searchCities returns Success`() = runTest {
        val response = listOf(
            GeoCityDto(name = "Barcelona", lat = 41.38, lon = 2.17, country = "ES", state = "Catalonia")
        )

        whenever(api.searchCities(query = "Barc", limit = 5)).thenReturn(response)

        val result = repo.searchCities("Barc", 5)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        verify(api).searchCities(query = "Barc", limit = 5)
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `searchCities returns Error when api throws`() = runTest {
        whenever(api.searchCities(query = any(), limit = any()))
            .thenThrow(RuntimeException("boom"))

        val result = repo.searchCities("X", 5)

        assertTrue(result is AppResult.Error)
        assertEquals(ErrorType.Unknown, (result as AppResult.Error).type)
    }

    @Test
    fun `getWeek returns Error when no selected city`() = runTest {
        val result = repo.getWeek()

        assertTrue(result is AppResult.Error)
        assertEquals(ErrorType.Unknown, (result as AppResult.Error).type)
        verifyNoInteractions(api)
    }

    @Test
    fun `getWeek groups by day, sorts keys and filters empty weather`() = runTest {
        prefs.saveCity(SelectedCity("Saved", 10.0, 20.0))

        val list = buildForecastItems(days = 3, includeEmptyWeatherItem = true)
        val dto = WeekWeatherDto(
            city = dummyCity(),
            cnt = list.size,
            cod = "200",
            list = list,
            message = 0
        )

        whenever(api.getSevenDayForecastByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "en"))
            .thenReturn(dto)

        val result = repo.getWeek()

        assertTrue(result is AppResult.Success)
        val grouped = (result as AppResult.Success).data

        // keys sorted yyyy-MM-dd
        val keys = grouped.keys.toList()
        assertEquals(keys.sorted(), keys)

        // filtered: no item with empty weather
        grouped.values.flatten().forEach { item ->
            assertTrue(item.weather.isNotEmpty())
        }

        verify(api).getSevenDayForecastByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "en")
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `getWeek limits to 7 distinct days`() = runTest {
        prefs.saveCity(SelectedCity("Saved", 10.0, 20.0))

        val list = buildForecastItems(days = 10, includeEmptyWeatherItem = false)
        val dto = WeekWeatherDto(
            city = dummyCity(),
            cnt = list.size,
            cod = "200",
            list = list,
            message = 0
        )

        whenever(api.getSevenDayForecastByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "en"))
            .thenReturn(dto)

        val result = repo.getWeek()

        assertTrue(result is AppResult.Success)
        val grouped = (result as AppResult.Success).data

        assertEquals(7, grouped.size)
        verify(api).getSevenDayForecastByCoords(lat = 10.0, lon = 20.0, units = "metric", lang = "en")
    }

    // -------- helpers --------

    private fun buildForecastItems(days: Int, includeEmptyWeatherItem: Boolean): List<ForecastItem> {
        val items = mutableListOf<ForecastItem>()
        for (d in 1..days) {
            val day = if (d < 10) "0$d" else "$d"
            val date = "2026-02-$day"

            items += forecastItem("$date 12:00:00", hasWeather = true, tempMin = 10.0 + d, tempMax = 15.0 + d)
            items += forecastItem("$date 15:00:00", hasWeather = true, tempMin = 11.0 + d, tempMax = 16.0 + d)

            if (includeEmptyWeatherItem && d == 1) {
                items += forecastItem("$date 18:00:00", hasWeather = false, tempMin = 0.0, tempMax = 0.0)
            }
        }
        return items
    }

    private fun forecastItem(
        dateTime: String,
        hasWeather: Boolean,
        tempMin: Double,
        tempMax: Double
    ): ForecastItem {
        return ForecastItem(
            clouds = Clouds(all = 50),
            dt = 0,
            dt_txt = dateTime,
            main = Main(
                feels_like = tempMax,
                grnd_level = 0,
                humidity = 80,
                pressure = 1000,
                sea_level = 1000,
                temp = tempMax,
                temp_kf = 0.0,
                temp_max = tempMax,
                temp_min = tempMin
            ),
            pop = 0.0,
            rain = Rain(0.0),
            sys = Sys(pod = "d"),
            visibility = 10000,
            weather = if (hasWeather) listOf(Weather("light rain", "10d", 500, "Rain")) else emptyList(),
            wind = Wind(deg = 0, gust = 0.0, speed = 1.0)
        )
    }

    private fun dummyCity(): City = City(
        coord = Coord(lat = 10.0, lon = 20.0),
        country = "ES",
        id = 1,
        name = "X",
        population = 0,
        sunrise = 0,
        sunset = 0,
        timezone = 0
    )
}