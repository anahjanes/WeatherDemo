package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.ErrorType
import com.anahjanes.core_domain.model.WeeklyForecastDay
import com.anahjanes.core_domain.repository.WeatherRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class GetWeekWeatherUseCaseTest {

    private val repo: WeatherRepository = mock()

    private lateinit var useCase: GetWeekWeatherUseCase

    @Before
    fun setup() {
        useCase = GetWeekWeatherUseCase(repo)
    }

    @Test
    fun `returns success result from repository`() = runTest {

        val forecast = listOf(
            WeeklyForecastDay(
                dateKey = "2025-01-01",
                minTempC = 10.0,
                maxTempC = 20.0,
                conditionDescription = "Sunny",
                iconCode = "01d"
            )
        )

        whenever(repo.getWeekByCoords(10.0, 20.0))
            .thenReturn(AppResult.Success(forecast))

        val result = useCase(10.0, 20.0)

        assertTrue(result is AppResult.Success)
        assertEquals(forecast, (result as AppResult.Success).data)

        verify(repo).getWeekByCoords(10.0, 20.0)
    }

    @Test
    fun `returns error result from repository`() = runTest {

        val error = AppResult.Error(
            type = ErrorType.Network,
            message = "No internet",
            cause = null
        )

        whenever(repo.getWeekByCoords(10.0, 20.0))
            .thenReturn(error)

        val result = useCase(10.0, 20.0)

        assertEquals(error, result)
        verify(repo).getWeekByCoords(10.0, 20.0)
    }
}
