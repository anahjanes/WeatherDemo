package com.anahjanes.feature_weather.city.model

import com.anahjanes.core_domain.model.CityResult
import org.junit.Assert.assertEquals
import org.junit.Test

class CityMapperTest {

    @Test
    fun `toUiItem should map  to CityUiModel correctly`() {
        // Given
        val cityResult = CityResult(
            name = "London",
            country = "GB",
            lat = 51.5074,
            lon = -0.1278
        )

        // When
        val cityUiModel = cityResult.toUiItem()

        // Then
        assertEquals("London", cityUiModel.name)
        assertEquals("GB", cityUiModel.country)
        assertEquals(51.5074, cityUiModel.lat, 0.0)
        assertEquals(-0.1278, cityUiModel.lon, 0.0)
    }
}