package com.anahjanes.feature_weather.home.model

import com.anahjanes.core.data.remote.dto.CloudsDto
import com.anahjanes.core.data.remote.dto.CoordDto
import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.dto.MainWeatherDto
import com.anahjanes.core.data.remote.dto.WeatherDescriptionDto
import com.anahjanes.core.data.remote.dto.WindDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeMappersTest {

    @Test
    fun `toHomeUiModel should map CurrentWeatherDto to HomeUiModel correctly`() {
        // Given
        val currentWeatherDto = CurrentWeatherDto(
            name = "London",
            dt = 1678886400, // March 15, 2023 12:00:00 PM UTC
            weather = listOf(WeatherDescriptionDto(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            main = MainWeatherDto(temp = 10.0, feels_like = 8.0, temp_max = 12.0, temp_min = 6.0, humidity = 50),
            clouds = CloudsDto(all = 10),
            wind = WindDto(speed = 5.0),
            coord = CoordDto(lat = 51.5074, lon = -0.1278)
        )

        // When
        val homeUiModel = currentWeatherDto.toHomeUiModel()

        // Then
        val expectedDate = SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH).format(Date(1678886400L * 1000))
        assertEquals("London", homeUiModel.city)
        assertEquals(expectedDate, homeUiModel.dateText)
        assertEquals("10°", homeUiModel.temperature)
        assertEquals("Clear sky", homeUiModel.condition)
        assertEquals("8°C", homeUiModel.feelsLike)
        assertEquals("https://openweathermap.org/img/wn/01d@4x.png", homeUiModel.iconUrl)
        assertEquals("12°", homeUiModel.tempMax)
        assertEquals("6°", homeUiModel.tempMin)
        assertEquals("10%", homeUiModel.clouds)
        assertEquals("18 km/h", homeUiModel.wind)
        assertEquals("50%", homeUiModel.humidity)
    }
}