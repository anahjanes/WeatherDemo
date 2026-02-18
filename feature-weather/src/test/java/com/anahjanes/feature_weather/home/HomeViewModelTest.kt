
import app.cash.turbine.test
import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.usecases.GetSelectedCityUseCase
import com.anahjanes.core_domain.usecases.GetTodayWeatherUseCase
import com.anahjanes.feature_weather.MainDispatcherRule
import com.anahjanes.feature_weather.home.HomeEvent
import com.anahjanes.feature_weather.home.HomeUiState
import com.anahjanes.feature_weather.home.HomeViewModel
import com.anahjanes.feature_weather.location.LatLon
import com.anahjanes.feature_weather.location.LocationDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSelectedCity: GetSelectedCityUseCase = mock()
    private val getTodayWeather: GetTodayWeatherUseCase = mock()
    private val locationDataSource: LocationDataSource = mock()

    private fun createVm() = HomeViewModel(
        getSelectedCity = getSelectedCity,
        getTodayWeather = getTodayWeather,
        locationDataSource = locationDataSource,
    )

    @Test
    fun `loadWeather - saved city exists - uses its coords and sets Success`() = runTest {
        whenever(getSelectedCity()).thenReturn(SelectedCity(name = "BCN", lat = 1.0, lon = 2.0))
        whenever(getTodayWeather(1.0, 2.0)).thenReturn(AppResult.Success(dummyCurrentWeather()))

        val vm = createVm()

        vm.loadWeather()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is HomeUiState.Success)
        verify(getTodayWeather).invoke(1.0, 2.0)
        verify(locationDataSource, never()).hasLocationPermission()
        verify(locationDataSource, never()).getCurrentLocation()
    }

    @Test
    fun `loadWeather - no saved city and no permission - emits RequestLocationPermission`() = runTest {
        whenever(getSelectedCity()).thenReturn(null)
        whenever(locationDataSource.hasLocationPermission()).thenReturn(false)

        val vm = createVm()

        vm.events.test {
            vm.loadWeather()
            advanceUntilIdle()

            assertEquals(HomeEvent.RequestLocationPermission, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(locationDataSource).hasLocationPermission()
        verifyNoInteractions(getTodayWeather)

        assertEquals(HomeUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `loadWeather - no saved city, has permission, location fails - sets Error`() = runTest {
        whenever(getSelectedCity()).thenReturn(null)
        whenever(locationDataSource.hasLocationPermission()).thenReturn(true)
        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.failure(IllegalStateException("no loc")))

        val vm = createVm()

        vm.loadWeather()
        advanceUntilIdle()

        assertEquals(HomeUiState.Error, vm.uiState.value)
        verify(locationDataSource).getCurrentLocation()
        verifyNoInteractions(getTodayWeather)
    }

    @Test
    fun `loadWeather - no saved city, has permission, location ok, weather success - sets Success`() = runTest {
        whenever(getSelectedCity()).thenReturn(null)
        whenever(locationDataSource.hasLocationPermission()).thenReturn(true)
        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.success(LatLon(lat = 10.0, lon = 20.0)))
        whenever(getTodayWeather(10.0, 20.0)).thenReturn(AppResult.Success(dummyCurrentWeather()))

        val vm = createVm()

        vm.loadWeather()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is HomeUiState.Success)
        verify(getTodayWeather).invoke(10.0, 20.0)
    }

    @Test
    fun `onPermissionGranted - location ok and weather success - sets Success`() = runTest {
        whenever(locationDataSource.getCurrentLocation())
            .thenReturn(Result.success(LatLon(10.0, 20.0)))
        whenever(getTodayWeather(10.0, 20.0)).thenReturn(AppResult.Success(dummyCurrentWeather()))

        val vm = createVm()

        vm.onPermissionGranted()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is HomeUiState.Success)
        verify(locationDataSource).getCurrentLocation()
        verify(getTodayWeather).invoke(10.0, 20.0)
    }

    @Test
    fun `onPermissionDenied - sets NeedsLocationPermission`() = runTest {
        val vm = createVm()
        vm.onPermissionDenied()
        assertEquals(HomeUiState.NeedsLocationPermission, vm.uiState.value)
    }

    private fun dummyCurrentWeather(
        cityName: String = "Barcelona"
    ): CurrentWeather =
        CurrentWeather(
            cityName = cityName,
            timestampSeconds = 1_700_000_000L,

            temperatureC = 20.0,
            feelsLikeC = 19.5,
            tempMinC = 18.0,
            tempMaxC = 22.0,

            conditionDescription = "clear sky",
            iconCode = "01d",
            humidityPct = 60,
            windSpeedMs = 3.5,
            cloudsPct = 10
        )
}
