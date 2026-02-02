package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify


@OptIn(ExperimentalCoroutinesApi::class)
class SaveCityUseCaseTest {

    private val repository: WeatherRepository = mock()
    private lateinit var useCase: SaveCityUseCase

    @Before
    fun setup() {
        useCase = SaveCityUseCase(repository)
    }

    @Test
    fun `invokes repository saveCity with correct city`() = runTest {
        val city = SelectedCity("Madrid", 40.0, -3.0)

        useCase(city)

        verify(repository).saveCity(city)
    }
}
