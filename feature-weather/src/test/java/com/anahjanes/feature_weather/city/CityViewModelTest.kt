package com.anahjanes.feature_weather.city

import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.ErrorType
import com.anahjanes.core.data.remote.dto.GeoCityDto
import com.anahjanes.feature_weather.MainDispatcherRule
import com.anahjanes.feature_weather.city.model.CityUiModel
import com.anahjanes.feature_weather.city.model.toUiItem
import com.anahjanes.feature_weather.location.LatLon
import com.anahjanes.feature_weather.location.LocationDataSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: WeatherRepository = mock()
    private val locationDataSource: LocationDataSource = mock()

    private lateinit var viewModel: CityViewModel

    // Para controlar el flow que emite la ciudad seleccionada
    private val selectedCityFlow = MutableStateFlow<SelectedCity?>(null)

    @Before
    fun setUp() {
        whenever(repository.observeSelectedCity()).thenReturn(selectedCityFlow)
        viewModel = CityViewModel(repository, locationDataSource)
    }

    @Test
    fun `init - observes current city and updates currentCity in uiState`() = runTest {
        // When
        selectedCityFlow.value = SelectedCity(name = "Barcelona", lat = 1.0, lon = 2.0)
        advanceUntilIdle()

        // Then
        assertEquals("Barcelona", viewModel.uiState.value.currentCity)

        // When (null clears)
        selectedCityFlow.value = null
        advanceUntilIdle()

        // Then
        assertEquals(null, viewModel.uiState.value.currentCity)

        verify(repository).observeSelectedCity()
    }

    @Test
    fun `onSearchQueryChanged - updates searchQuery and clears error`() = runTest {

        whenever(repository.searchCities(any(), any())).thenReturn(
            AppResult.Error(type = ErrorType.Http, message = "boom")
        )

        viewModel.onSearchQueryChanged("abc")
        viewModel.searchCity()
        advanceUntilIdle()
        assertEquals("boom", viewModel.uiState.value.error)

        // When
        viewModel.onSearchQueryChanged("madrid")
        advanceUntilIdle()

        // Then
        assertEquals("madrid", viewModel.uiState.value.searchQuery)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `searchCity - when query length less than 3, does nothing`() = runTest {
        // Given
        viewModel.onSearchQueryChanged("ab")
        advanceUntilIdle()

        // When
        viewModel.searchCity()
        advanceUntilIdle()

        // Then
        verify(repository, never()).searchCities(any(), any())
        assertFalse(viewModel.uiState.value.isSearching)
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `searchCity - success updates results and stops searching`() = runTest {
        // Given
        val results = listOf(
            GeoCityDto(name = "Barcelona", lat = 41.38, lon = 2.17, country = "ES", state = "Catalunya"),
            GeoCityDto(name = "Madrid", lat = 40.41, lon = -3.70, country = "ES", state = null),
        )

        whenever(repository.searchCities("bar", limit = 10))
            .thenReturn(AppResult.Success(results))

        viewModel.onSearchQueryChanged("bar")
        advanceUntilIdle()

        // When
        viewModel.searchCity()
        advanceUntilIdle()
        val expectedResults = results.map { it.toUiItem() }

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(expectedResults, state.results)
        assertEquals(null, state.error)

        verify(repository).searchCities("bar", limit = 10)
    }

    @Test
    fun `searchCity - error sets error message and stops searching`() = runTest {
        // Given
        whenever(repository.searchCities("bar", limit = 10))
            .thenReturn(AppResult.Error(type = ErrorType.Http, message = "HTTP 500"))

        viewModel.onSearchQueryChanged("bar")
        advanceUntilIdle()

        // When
        viewModel.searchCity()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals("HTTP 500", state.error)
        assertTrue(state.results.isEmpty())
    }

    @Test
    fun `onCitySelected - saves selected city with built name and sets citySelected true, clears query`() = runTest {
        // Given
        val city = CityUiModel(
            name = "Barcelona",
            country = "ES",
            lat = 41.38,
            lon = 2.17
        )

        // When
        viewModel.onSearchQueryChanged("bar")
        viewModel.onCitySelected(city)
        advanceUntilIdle()

        // Then
        val expectedName = "Barcelona, Catalunya, ES"
        verify(repository).saveCity(
            SelectedCity(
                name = expectedName,
                lat = 41.38,
                lon = 2.17
            )
        )

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.citySelected)
    }

    @Test
    fun `onUseCurrentLocation - success saves city with empty name and sets citySelected true, clears query`() = runTest {
        // Given
        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.success(LatLon(lat = 41.38, lon = 2.17)))

        viewModel.onSearchQueryChanged("bar")
        advanceUntilIdle()

        // When
        viewModel.onUseCurrentLocation()
        advanceUntilIdle()

        // Then
        verify(locationDataSource).getCurrentLocation()
        verify(repository).saveCity(
            SelectedCity(name = "", lat = 41.38, lon = 2.17)
        )

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.citySelected)
    }

    @Test
    fun `onUseCurrentLocation - failure does not save city and does not mark citySelected`() = runTest {
        // Given
        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.failure(IllegalStateException("no gps")))

        // When
        viewModel.onUseCurrentLocation()
        advanceUntilIdle()

        // Then
        verify(locationDataSource).getCurrentLocation()
        verify(repository, never()).saveCity(any())

        assertFalse(viewModel.uiState.value.citySelected)
    }

    @Test
    fun `onNavigationHandled - resets citySelected to false`() = runTest {
        // Given: forzamos citySelected=true seleccionando una ciudad
        val city =
            CityUiModel(name = "Madrid",  country = "ES", lat = 40.41, lon = -3.70)
        viewModel.onCitySelected(city)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.citySelected)

        // When
        viewModel.onNavigationHandled()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.citySelected)
    }
}