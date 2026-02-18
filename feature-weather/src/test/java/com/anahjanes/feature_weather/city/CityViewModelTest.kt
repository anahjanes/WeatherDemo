
import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CityResult
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.usecases.ObserveSelectedCityUseCase
import com.anahjanes.core_domain.usecases.SaveCityUseCase
import com.anahjanes.core_domain.usecases.SearchCitiesUseCase
import com.anahjanes.feature_weather.MainDispatcherRule
import com.anahjanes.feature_weather.city.CityViewModel
import com.anahjanes.feature_weather.city.model.CityUiModel
import com.anahjanes.feature_weather.location.LatLon
import com.anahjanes.feature_weather.location.LocationDataSource
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import app.cash.turbine.test
import com.anahjanes.feature_weather.city.CityUiState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest


@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

class CityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val locationDataSource: LocationDataSource = mock()

    private val searchCitiesUseCase: SearchCitiesUseCase = mock()
    private val observeSelectedCityUseCase: ObserveSelectedCityUseCase = mock()
    private val saveCityUseCase: SaveCityUseCase = mock()

    private lateinit var viewModel: CityViewModel

    @Test
    fun `init - when observeSelectedCity emits city with name - updates currentCity`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(
            flowOf(
                SelectedCity(
                    name = "Barcelona",
                    lat = 1.0,
                    lon = 2.0
                )
            )
        )

        viewModel = createVm()
        advanceUntilIdle()

        assertEquals("Barcelona", viewModel.uiState.value.currentCity)
    }

    @Test
    fun `init - when observeSelectedCity emits city with blank name - currentCity becomes null`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(
            flowOf(
                SelectedCity(
                    name = "   ",
                    lat = 1.0,
                    lon = 2.0
                )
            )
        )

        viewModel = createVm()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.currentCity)
    }

    @Test
    fun `onSearchQueryChanged - updates searchQuery and clears error`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))
        whenever(searchCitiesUseCase("bar")).thenReturn(AppResult.Error(ErrorType.Network))

        viewModel = createVm()

        // First force an error
        viewModel.onSearchQueryChanged("bar")
        viewModel.searchCity()
        advanceUntilIdle()

        assertEquals("Unknown error", viewModel.uiState.value.error)

        // When
        viewModel.onSearchQueryChanged("bara")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("bara", state.searchQuery)
        assertNull(state.error)
    }

    @Test
    fun `searchCity - query shorter than 3 does nothing`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))

        viewModel = createVm()
        viewModel.onSearchQueryChanged("ab")
        advanceUntilIdle()

        viewModel.searchCity()
        advanceUntilIdle()

        verifyNoInteractions(searchCitiesUseCase)
        assertFalse(viewModel.uiState.value.isSearching)
        assertTrue(viewModel.uiState.value.results.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `searchCity - success updates results and stops searching`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))

        val domainCities = listOf(
            CityResult(name = "Barcelona", country = "ES", lat = 41.0, lon = 2.0),
            CityResult(name = "Barakaldo", country = "ES", lat = 43.0, lon = -2.9),
        )
        whenever(searchCitiesUseCase("bar")).thenReturn(AppResult.Success(domainCities))

        viewModel = createVm()
        viewModel.onSearchQueryChanged("bar")

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.searchCity()

            // Consume emissions until searching finishes (StateFlow may skip intermediate searching=true)
            var state: CityUiState
            do {
                state = awaitItem()
            } while (state.isSearching)

            assertNull(state.error)
            assertEquals(2, state.results.size)

            cancelAndIgnoreRemainingEvents()
        }

        verify(searchCitiesUseCase).invoke("bar")
    }

    @Test
    fun `searchCity - error sets Unknown error and stops searching`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))
        whenever(searchCitiesUseCase("bar")).thenReturn(AppResult.Error(ErrorType.Network))

        viewModel = createVm()
        viewModel.onSearchQueryChanged("bar")

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.searchCity()

            var state: CityUiState
            do {
                state = awaitItem()
            } while (state.isSearching)

            assertEquals("Unknown error", state.error)
            assertTrue(state.results.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }

        verify(searchCitiesUseCase).invoke("bar")
    }

    @Test
    fun `onCitySelected - saves city with built name and updates ui flags`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))
        viewModel = createVm()

        val uiCity = CityUiModel(
            name = "Barcelona",
            country = "ES",
            lat = 41.3874,
            lon = 2.1686
        )

        viewModel.onCitySelected(uiCity)
        advanceUntilIdle()

        val captor = argumentCaptor<SelectedCity>()
        verify(saveCityUseCase).invoke(captor.capture())

        val saved = captor.firstValue
        assertEquals("Barcelona, ES", saved.name)
        assertEquals(41.3874, saved.lat, 0.0001)
        assertEquals(2.1686, saved.lon, 0.0001)

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.citySelected)
    }

    @Test
    fun `onUseCurrentLocation - success saves blank name city and updates ui flags`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))

        whenever(locationDataSource.getCurrentLocation()).thenReturn(
            Result.success(
                LatLon(
                    lat = 10.0,
                    lon = 20.0
                )
            )
        )

        viewModel = createVm()

        viewModel.onUseCurrentLocation()
        advanceUntilIdle()

        val captor = argumentCaptor<SelectedCity>()
        verify(saveCityUseCase).invoke(captor.capture())

        val saved = captor.firstValue
        assertEquals("", saved.name)
        assertEquals(10.0, saved.lat, 0.0001)
        assertEquals(20.0, saved.lon, 0.0001)

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.citySelected)
    }

    @Test
    fun `onUseCurrentLocation - failure does not save and does not set citySelected`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))

        whenever(locationDataSource.getCurrentLocation()).thenReturn(
            Result.failure(IllegalStateException("no loc"))
        )

        viewModel = createVm()

        viewModel.onUseCurrentLocation()
        advanceUntilIdle()

        verify(saveCityUseCase, never()).invoke(any())
        assertFalse(viewModel.uiState.value.citySelected)
    }

    @Test
    fun `onNavigationHandled - resets citySelected and clears results`() = runTest {
        whenever(observeSelectedCityUseCase()).thenReturn(flowOf(null))
        viewModel = createVm()

        viewModel.onSearchQueryChanged("bar")

        whenever(searchCitiesUseCase("bar")).thenReturn(
            AppResult.Success(listOf(CityResult("Barcelona", "ES", 1.0, 2.0)))
        )

        viewModel.searchCity()
        advanceUntilIdle()

        viewModel.onCitySelected(CityUiModel("Barcelona", "ES", 1.0, 2.0))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.citySelected)
        assertTrue(viewModel.uiState.value.results.isNotEmpty())

        viewModel.onNavigationHandled()

        val state = viewModel.uiState.value
        assertFalse(state.citySelected)
        assertTrue(state.results.isEmpty())
    }

    private fun createVm(): CityViewModel =
        CityViewModel(
            searchCitiesUseCase = searchCitiesUseCase,
            observeSelectedCityUseCase = observeSelectedCityUseCase,
            saveCityUseCase = saveCityUseCase,
            locationDataSource = locationDataSource,
        )
}