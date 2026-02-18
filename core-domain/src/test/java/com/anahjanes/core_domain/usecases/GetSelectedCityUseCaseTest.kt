package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class GetSelectedCityUseCaseTest {

    private val repo: WeatherRepository = mock()

    private lateinit var useCase: GetSelectedCityUseCase

    @Before
    fun setup() {
        useCase = GetSelectedCityUseCase(repo)
    }

    @Test
    fun `invoke returns selected city from repository`() = runTest {
        val city = SelectedCity("Madrid", 40.0, -3.0)

        whenever(repo.getSelectedCity()).thenReturn(city)

        val result = useCase()

        assertEquals(city, result)
        verify(repo).getSelectedCity()
    }

    @Test
    fun `invoke returns null when repository returns null`() = runTest {

        whenever(repo.getSelectedCity()).thenReturn(null)

        val result = useCase()

        assertNull(result)
        verify(repo).getSelectedCity()
    }
}
