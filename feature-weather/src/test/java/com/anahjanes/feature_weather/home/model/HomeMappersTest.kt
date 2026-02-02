
package com.anahjanes.feature_weather.home.model


import com.anahjanes.core_domain.model.CurrentWeather
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeMappersTest {
    @Test
    fun `toHomeUiModel maps all fields correctly`() {
        // Given
        val timestamp = 1_700_000_000L // fixed for stable test

        val current = CurrentWeather(
            cityName = "Barcelona",
            timestampSeconds = timestamp,
            temperatureC = 20.4,
            feelsLikeC = 19.6,
            tempMinC = 18.2,
            tempMaxC = 22.9,
            conditionDescription = "clear sky",
            iconCode = "01d",
            humidityPct = 60,
            windSpeedMs = 5.0,   // 5 * 3.6 = 18 km/h
            cloudsPct = 25
        )

        // When
        val ui = current.toHomeUiModel()

        // Then
        assertEquals("Barcelona", ui.city)

        // Date
        val expectedDate = SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH)
            .format(Date(timestamp * 1000))
        assertEquals(expectedDate, ui.dateText)

        // Temperature rounding
        assertEquals("20°", ui.temperature)      // 20.4 -> 20
        assertEquals("20°C", ui.feelsLike)       // 19.6 -> 20
        assertEquals("23°", ui.tempMax)          // 22.9 -> 23
        assertEquals("18°", ui.tempMin)          // 18.2 -> 18

        // Condition capitalized
        assertEquals("Clear sky", ui.condition)

        // Wind conversion (m/s -> km/h)
        assertEquals("18 km/h", ui.wind)

        // Humidity and clouds
        assertEquals("60%", ui.humidity)
        assertEquals("25%", ui.clouds)

        // Icon URL (we don't check the exact URL because it depends on the extension)
        assertNotNull(ui.iconUrl)
    }

    @Test
    fun `toHomeUiModel sets condition dash when description is null`() {
        val current = CurrentWeather(
            cityName = "Barcelona",
            timestampSeconds = 1_700_000_000L,
            temperatureC = 20.0,
            feelsLikeC = 20.0,
            tempMinC = 18.0,
            tempMaxC = 22.0,
            conditionDescription = null,
            iconCode = null,
            humidityPct = 50,
            windSpeedMs = 0.0,
            cloudsPct = 0
        )

        val ui = current.toHomeUiModel()

        assertEquals("-", ui.condition)
        assertNull(ui.iconUrl)
    }

    @Test
    fun `toHomeUiModel rounds wind correctly`() {
        val current = CurrentWeather(
            cityName = "Test",
            timestampSeconds = 1_700_000_000L,
            temperatureC = 0.0,
            feelsLikeC = 0.0,
            tempMinC = 0.0,
            tempMaxC = 0.0,
            conditionDescription = "rain",
            iconCode = null,
            humidityPct = 0,
            windSpeedMs = 2.78, // 2.78 * 3.6 = 10.008 -> 10
            cloudsPct = 0
        )

        val ui = current.toHomeUiModel()

        assertEquals("10 km/h", ui.wind)
    }
}
