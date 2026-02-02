package com.anahjanes.feature_weather.week

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.usecases.GetSelectedCityUseCase
import com.anahjanes.core_domain.usecases.GetWeekWeatherUseCase
import com.anahjanes.feature_weather.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core_domain.model.WeeklyForecastDay
import com.anahjanes.feature_weather.week.model.WeekUiModel

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WeekViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getWeekWeather: GetWeekWeatherUseCase = mock()
    private val getSelectedCityUseCase: GetSelectedCityUseCase = mock()

    private fun createVm() = WeekViewModel(
        getWeekWeather = getWeekWeather,
        getSelectedCityUseCase = getSelectedCityUseCase
    )

    @Test
    fun `loadWeek - when no city selected - sets Error NO_CITY_SELECTED and does not call getWeekWeather`() = runTest {
        // Given
        whenever(getSelectedCityUseCase()).thenReturn(null)
        val vm = createVm()

        // When
        vm.loadWeek()
        advanceUntilIdle()

        // Then
        val state = vm.uiState.value
        assertTrue(state is WeekUiState.Error)
        state as WeekUiState.Error
        assertEquals(ErrorWeek.NO_CITY_SELECTED, state.message)

        verifyNoInteractions(getWeekWeather)
    }

    @Test
    fun `loadWeek - when city selected and week weather success - sets Success with city and mapped items`() = runTest {
        // Given
        val city = SelectedCity(name = "Barcelona", lat = 1.0, lon = 2.0)
        whenever(getSelectedCityUseCase()).thenReturn(city)

        val domainWeek = listOf(
            WeeklyForecastDay(
                dateKey = "2026-02-18",
                minTempC = 10.2,
                maxTempC = 20.9,
                conditionDescription = "clear sky",
                iconCode = "01d"
            ),
            WeeklyForecastDay(
                dateKey = "2026-02-19",
                minTempC = 9.0,
                maxTempC = 18.4,
                conditionDescription = "few clouds",
                iconCode = "02d"
            )
        )

        whenever(getWeekWeather(1.0, 2.0)).thenReturn(AppResult.Success(domainWeek))

        val vm = createVm()

        // When
        vm.loadWeek()
        advanceUntilIdle()

        // Then
        val state = vm.uiState.value
        assertTrue(state is WeekUiState.Success)
        state as WeekUiState.Success

        assertEquals("Barcelona", state.city)
        assertEquals(2, state.items.size)


        val first: WeekUiModel = state.items.first()
        assertEquals("clear sky", first.weather)
        assertEquals(20, first.maxTemp) // 20.9 -> 20
        assertEquals(10, first.minTemp) // 10.2 -> 10
        assertNotNull(first.day)
        assertNotNull(first.date)
        assertNotNull(first.iconUrl)

        verify(getWeekWeather).invoke(1.0, 2.0)
    }

    @Test
    fun `loadWeek - when city selected and week weather error - sets Error LOAD_WEEK_ERROR`() = runTest {
        // Given
        val city = SelectedCity(name = "Barcelona", lat = 1.0, lon = 2.0)
        whenever(getSelectedCityUseCase()).thenReturn(city)

        whenever(getWeekWeather(1.0, 2.0)).thenReturn(AppResult.Error(ErrorType.Network))

        val vm = createVm()

        // When
        vm.loadWeek()
        advanceUntilIdle()

        // Then
        val state = vm.uiState.value
        assertTrue(state is WeekUiState.Error)
        state as WeekUiState.Error
        assertEquals(ErrorWeek.LOAD_WEEK_ERROR, state.message)

        verify(getWeekWeather).invoke(1.0, 2.0)
    }
}