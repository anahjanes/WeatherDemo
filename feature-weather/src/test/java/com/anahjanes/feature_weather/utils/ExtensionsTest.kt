package com.anahjanes.feature_weather.utils

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `toWeatherIconUrl returns correct url`() {
        val icon = "01d"
        val result = icon.toWeatherIconUrl()

        assertEquals("https://openweathermap.org/img/wn/01d@4x.png", result)
    }

    @Test
    fun `toShortDate formats correctly`() {
        val date = "2024-02-10"
        val result = date.toShortDate()

        assertEquals("10 Feb", result)
    }

    @Test
    fun `toShortDate returns same string when invalid`() {
        val date = "invalid"
        val result = date.toShortDate()

        assertEquals("invalid", result)
    }
}