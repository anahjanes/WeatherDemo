package com.anahjanes.feature_weather.week

import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.dto.City
import com.anahjanes.core.data.remote.dto.Clouds
import com.anahjanes.core.data.remote.dto.CloudsDto
import com.anahjanes.core.data.remote.dto.Coord
import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.core.data.remote.dto.Main
import com.anahjanes.core.data.remote.dto.MainWeatherDto
import com.anahjanes.core.data.remote.dto.Rain
import com.anahjanes.core.data.remote.dto.Sys
import com.anahjanes.core.data.remote.dto.Weather
import com.anahjanes.core.data.remote.dto.WeatherDescriptionDto
import com.anahjanes.core.data.remote.dto.Wind
import com.anahjanes.core.data.remote.dto.WindDto
import com.anahjanes.feature_weather.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WeekViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: WeatherRepository = mock()

    private lateinit var viewModel: WeekViewModel

    @Before
    fun setUp() {
        viewModel = WeekViewModel(repository)
    }

    @Test
    fun `loadWeek - when no city selected, emits Error NO_CITY_SELECTED`() = runTest {
        // Given
        whenever(repository.observeSelectedCity()).thenReturn(flowOf(null))

        // When
        viewModel.loadWeek()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeekUiState.Error)
        assertEquals(
            ErrorWeek.NO_CITY_SELECTED,
            (state as WeekUiState.Error).message
        )

        verify(repository).observeSelectedCity()
        verify(repository, never()).getWeek()
    }

    @Test
    fun `loadWeek - when repository returns Error, emits Error LOAD_WEEK_ERROR`() = runTest {
        // Given
        whenever(repository.observeSelectedCity())
            .thenReturn(flowOf(sampleCity()))

        whenever(repository.getWeek())
            .thenReturn(AppResult.Error(type = ErrorType.Unknown))

        // When
        viewModel.loadWeek()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeekUiState.Error)
        assertEquals(
            ErrorWeek.LOAD_WEEK_ERROR,
            (state as WeekUiState.Error).message
        )

        verify(repository).observeSelectedCity()
        verify(repository).getWeek()
    }

    @Test
    fun `loadWeek - when city exists and getWeek succeeds, emits Success`() = runTest {
        // Given
        whenever(repository.observeSelectedCity())
            .thenReturn(flowOf(sampleCity()))

        whenever(repository.getWeek())
            .thenReturn(AppResult.Success(fakeWeekMap()))

        // When
        viewModel.loadWeek()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeekUiState.Success)

        val success = state as WeekUiState.Success
        assertEquals("Barcelona", success.city)
        assertTrue(success.items.isNotEmpty())

        verify(repository).observeSelectedCity()
        verify(repository).getWeek()
    }

    // ───────── helpers ─────────

    private fun sampleCity(): SelectedCity =
        SelectedCity(
           lat = 41.38, lon = 2.17,
            name = "Barcelona",
        )

    private fun fakeWeekMap(): Map<String, List<ForecastItem>> {
        val item = ForecastItem(
            dt = 123456,
            dt_txt = "2026-02-05 12:00:00",
            main = Main(
                temp = 20.0,
                feels_like = 19.0,
                temp_min = 18.0,
                temp_max = 22.0,
                humidity = 50,
                pressure = 1013,
                sea_level = 1013,
                grnd_level = 1000,
                temp_kf = 0.0
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            wind = Wind(
                speed = 3.5,
                deg = 180,
                gust = 5.0
            ),
            clouds = Clouds(all = 10),
            pop = 0.0,
            rain = Rain(`3h` = 0.0),
            sys = Sys(pod = "d"),
            visibility = 10_000
        )

        return mapOf(
            "2026-02-05" to listOf(item)
        )
    }
}