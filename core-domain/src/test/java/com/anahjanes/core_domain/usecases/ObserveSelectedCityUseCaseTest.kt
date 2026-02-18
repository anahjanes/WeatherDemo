package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSelectedCityUseCaseTest {

    private val repo: WeatherRepository = mock()
    private lateinit var useCase: ObserveSelectedCityUseCase

    @Before
    fun setup() {
        useCase = ObserveSelectedCityUseCase(repo)
    }

    @Test
    fun `emits selected city from repository`() = runTest {
        val cityFlow = flowOf(
            SelectedCity("Madrid", 40.0, -3.0),
            null
        )

        whenever(repo.observeSelectedCity()).thenReturn(cityFlow)

        val resultFlow = useCase()

        val emissions = resultFlow.toList()

        assertEquals(listOf(
            SelectedCity("Madrid", 40.0, -3.0),
            null
        ), emissions)

        verify(repo).observeSelectedCity()
    }
}
