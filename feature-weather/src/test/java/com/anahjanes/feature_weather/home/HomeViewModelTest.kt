package com.anahjanes.feature_weather.home

import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.dto.CloudsDto
import com.anahjanes.core.data.remote.dto.CoordDto
import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.dto.MainWeatherDto
import com.anahjanes.core.data.remote.dto.WeatherDescriptionDto
import com.anahjanes.core.data.remote.dto.WindDto
import com.anahjanes.feature_weather.MainDispatcherRule
import com.anahjanes.feature_weather.location.LatLon
import com.anahjanes.feature_weather.location.LocationDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weatherRepository: WeatherRepository = mock()
    private val locationDataSource: LocationDataSource = mock()

    private lateinit var viewModel: HomeViewModel

    private fun createViewModel(): HomeViewModel =
        HomeViewModel(weatherRepository, locationDataSource)

    @Test
    fun `loadWeather - when city is saved, uses coords and emits Success`() = runTest {
        // Given
        whenever(weatherRepository.observeSelectedCity())
            .thenReturn(flowOf(SelectedCity("Saved", 10.0, 20.0)))

        val dto = sampleCurrentWeatherDto("Saved", 10.0, 20.0)
        whenever(weatherRepository.getTodayByCoords(10.0, 20.0))
            .thenReturn(AppResult.Success(dto))

        // When
        viewModel = createViewModel()
        advanceUntilIdle() // deja que stateIn(Eagerly) coja el valor

        viewModel.loadWeather()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        verify(weatherRepository).getTodayByCoords(10.0, 20.0)
        verify(locationDataSource, never()).getCurrentLocation()
    }

    @Test
    fun `loadWeather - when no city saved, gets location then calls weather by coords`() = runTest {
        // Given
        whenever(weatherRepository.observeSelectedCity()).thenReturn(flowOf(null))

        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.success(LatLon(lat = 41.38, lon = 2.17)))

        val dto = sampleCurrentWeatherDto("Barcelona", 41.38, 2.17)
        whenever(weatherRepository.getTodayByCoords(41.38, 2.17))
            .thenReturn(AppResult.Success(dto))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadWeather()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        verify(locationDataSource).getCurrentLocation()
        verify(weatherRepository).getTodayByCoords(41.38, 2.17)
    }

    @Test
    fun `loadWeather - when no city saved and location fails, emits Error and does not call repository`() = runTest {
        // Given
        whenever(weatherRepository.observeSelectedCity()).thenReturn(flowOf(null))

        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.failure(IllegalStateException("no gps")))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadWeather()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        verify(locationDataSource).getCurrentLocation()
        verify(weatherRepository, never()).getTodayByCoords(any(), any())
    }

    @Test
    fun `loadWeather - when repository returns Error, emits Error`() = runTest {
        // Given
        whenever(weatherRepository.observeSelectedCity())
            .thenReturn(flowOf(SelectedCity("Saved", 1.0, 2.0)))

        whenever(weatherRepository.getTodayByCoords(1.0, 2.0))
            .thenReturn(AppResult.Error(type = ErrorType.Http, message = "HTTP 401"))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadWeather()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        verify(weatherRepository).getTodayByCoords(1.0, 2.0)
        verify(locationDataSource, never()).getCurrentLocation()
    }

    // ───────── helpers ─────────

    private fun sampleCurrentWeatherDto(city: String, lat: Double, lon: Double): CurrentWeatherDto =
        CurrentWeatherDto(
            coord = CoordDto(lon = lon, lat = lat),
            weather = listOf(
                WeatherDescriptionDto(
                    id = 800,
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
            wind = WindDto(speed = 3.5),
            clouds = CloudsDto(all = 10),
            name = city,
            dt = 1234567890L
        )
}
