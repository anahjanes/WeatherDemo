
package com.anahjanes.feature_weather.week.model

import com.anahjanes.core_domain.model.WeeklyForecastDay
import org.junit.Assert.*
import org.junit.Test

class WeekMapperTest {

    @Test
    fun `toWeekUi maps list correctly`() {
        // Given
        val input = listOf(
            WeeklyForecastDay(
                dateKey = "2026-02-18",
                minTempC = 10.2,
                maxTempC = 20.9,
                conditionDescription = "clear sky",
                iconCode = "01d"
            ),
            WeeklyForecastDay(
                dateKey = "2026-02-19",
                minTempC = 5.7,
                maxTempC = 12.3,
                conditionDescription = null,
                iconCode = null
            )
        )

        // When
        val result = input.toWeekUi()

        // Then
        assertEquals(2, result.size)

        val first = result[0]
        assertEquals("clear sky", first.weather)
        assertEquals(20, first.maxTemp) // 20.9 -> 20
        assertEquals(10, first.minTemp) // 10.2 -> 10
        assertNotNull(first.iconUrl)

        assertTrue(first.day.isNotBlank())
        assertNotNull(first.date)
        assertTrue(first.date!!.isNotBlank())

        val second = result[1]
        assertEquals("", second.weather) // conditionDescription.orEmpty()
        assertEquals(12, second.maxTemp) // 12.3 -> 12
        assertEquals(5, second.minTemp)  // 5.7 -> 5
        assertNull(second.iconUrl)

        assertTrue(second.day.isNotBlank())
        assertNotNull(second.date)
        assertTrue(second.date!!.isNotBlank())
    }

    @Test
    fun `toWeekUi preserves order`() {
        // Given
        val input = listOf(
            WeeklyForecastDay(
                dateKey = "2026-02-18",
                minTempC = 1.0,
                maxTempC = 2.0,
                conditionDescription = "a",
                iconCode = null
            ),
            WeeklyForecastDay(
                dateKey = "2026-02-19",
                minTempC = 3.0,
                maxTempC = 4.0,
                conditionDescription = "b",
                iconCode = null
            )
        )

        // When
        val result = input.toWeekUi()

        // Then
        assertEquals("a", result[0].weather)
        assertEquals("b", result[1].weather)
    }

    @Test
    fun `toWeekUi converts doubles to int using truncation`() {
        // Given
        val input = listOf(
            WeeklyForecastDay(
                dateKey = "2026-02-18",
                minTempC = 9.99,
                maxTempC = 20.99,
                conditionDescription = "sun",
                iconCode = null
            )
        )

        // When
        val result = input.toWeekUi()

        // Then
        assertEquals(9, result[0].minTemp)
        assertEquals(20, result[0].maxTemp)
    }
}