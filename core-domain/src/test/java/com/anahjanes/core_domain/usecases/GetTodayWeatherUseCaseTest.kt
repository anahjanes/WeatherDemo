package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class GetTodayWeatherUseCaseTest {

    private val repo: WeatherRepository = mock()

    private lateinit var useCase: GetTodayWeatherUseCase

    @Before
    fun setup() {
        useCase = GetTodayWeatherUseCase(repo)
    }

    private fun successResult() =
        AppResult.Success(
            CurrentWeather(
                cityName = "Madrid",
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
        )

    // ----------------------------------------
    // 1️⃣ Returns result
    // ----------------------------------------

    @Test
    fun `returns result from repository`() = runTest {
        whenever(repo.getSelectedCity()).thenReturn(null)
        whenever(repo.getTodayByCoords(10.0, 20.0))
            .thenReturn(successResult())

        val result = useCase(10.0, 20.0)

        assertTrue(result is AppResult.Success)
    }

    // ----------------------------------------
    // 2️⃣ Saves when no city selected
    // ----------------------------------------

    @Test
    fun `saves city when none selected`() = runTest {

        whenever(repo.getSelectedCity()).thenReturn(null)
        whenever(repo.getTodayByCoords(10.0, 20.0))
            .thenReturn(successResult())

        useCase(10.0, 20.0)

        verify(repo).saveCity(
            SelectedCity(
                name = "Madrid",
                lat = 10.0,
                lon = 20.0
            )
        )
    }

    // ----------------------------------------
    // 3️⃣ Saves when same coords but blank name
    // ----------------------------------------

    @Test
    fun `saves city when same coords but blank name`() = runTest {

        whenever(repo.getSelectedCity()).thenReturn(
            SelectedCity(
                name = "",
                lat = 10.0,
                lon = 20.0
            )
        )

        whenever(repo.getTodayByCoords(10.0, 20.0))
            .thenReturn(successResult())

        useCase(10.0, 20.0)

        verify(repo).saveCity(
            SelectedCity(
                name = "Madrid",
                lat = 10.0,
                lon = 20.0
            )
        )
    }

    // ----------------------------------------
    // 4️⃣ Does NOT save when already valid city
    // ----------------------------------------

    @Test
    fun `does not save when city already valid`() = runTest {

        whenever(repo.getSelectedCity()).thenReturn(
            SelectedCity(
                name = "Madrid",
                lat = 10.0,
                lon = 20.0
            )
        )

        whenever(repo.getTodayByCoords(10.0, 20.0))
            .thenReturn(successResult())

        useCase(10.0, 20.0)

        verify(repo, never()).saveCity(any())
    }

    // ----------------------------------------
    // 5️⃣ Does NOT save when result is Error
    // ----------------------------------------

    @Test
    fun `does not save when result is error`() = runTest {

        whenever(repo.getSelectedCity()).thenReturn(null)

        whenever(repo.getTodayByCoords(10.0, 20.0))
            .thenReturn(
                AppResult.Error(
                    type = ErrorType.Network,
                    message = "No internet",
                    cause = null
                )
            )

        useCase(10.0, 20.0)

        verify(repo, never()).saveCity(any())
    }
}
