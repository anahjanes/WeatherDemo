package com.anahjanes.feature_weather.city.model

import com.anahjanes.core.data.remote.dto.GeoCityDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CityMapperTest {

    @Test
    fun `toUiItem should map GeoCityDto to CityUiModel correctly`() {
        // Given
        val geoCityDto = GeoCityDto(
            name = "London",
            country = "GB",
            lat = 51.5074,
            lon = -0.1278
        )

        // When
        val cityUiModel = geoCityDto.toUiItem()

        // Then
        assertEquals("London", cityUiModel.name)
        assertEquals("GB", cityUiModel.country)
        assertEquals(51.5074, cityUiModel.lat, 0.0)
        assertEquals(-0.1278, cityUiModel.lon, 0.0)
    }
}