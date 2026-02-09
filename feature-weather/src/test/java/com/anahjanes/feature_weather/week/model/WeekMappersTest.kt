package com.anahjanes.feature_weather.week.model

import com.anahjanes.core.data.remote.dto.Clouds
import com.anahjanes.core.data.remote.dto.ForecastItem
import com.anahjanes.core.data.remote.dto.Main
import com.anahjanes.core.data.remote.dto.Rain
import com.anahjanes.core.data.remote.dto.Sys
import com.anahjanes.core.data.remote.dto.Weather
import com.anahjanes.core.data.remote.dto.Wind
import org.junit.Assert.assertEquals
import org.junit.Test

class WeekMappersTest {

    @Test
    fun `toWeeklyForecast should map forecast items to weekly UI model correctly`() {
        // Given
        val forecastItems = mapOf(
            "2023-03-15" to listOf(
                ForecastItem(
                    dt = 1678886400,
                    dt_txt = "2023-03-15 12:00:00",
                    main = Main(temp_min = 5.0, temp_max = 15.0, temp = 10.0, feels_like = 8.0, humidity = 50, pressure = 1012, sea_level = 1012, grnd_level = 1012, temp_kf = 0.0),
                    weather = listOf(Weather(id = 500, main = "Rain", description = "light rain", icon = "10d")),
                    clouds = Clouds(all = 75),
                    wind = Wind(speed = 5.0, deg = 180, gust = 7.0),
                    visibility = 10000,
                    pop = 0.5,
                    rain = Rain(`3h` = 0.5),
                    sys = Sys(pod = "d")
                ),
                ForecastItem(
                    dt = 1678908000,
                    dt_txt = "2023-03-15 18:00:00",
                    main = Main(temp_min = 8.0, temp_max = 18.0, temp = 10.0, feels_like = 8.0, humidity = 50, pressure = 1012, sea_level = 1012, grnd_level = 1012, temp_kf = 0.0),
                    weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
                    clouds = Clouds(all = 10),
                    wind = Wind(speed = 2.0, deg = 180, gust = 4.0),
                    visibility = 10000,
                    pop = 0.1,
                    rain = Rain(`3h` = 0.0),
                    sys = Sys(pod = "d")
                )
            ),
            "2023-03-16" to listOf(
                ForecastItem(
                    dt = 1678972800,
                    dt_txt = "2023-03-16 12:00:00",
                    main = Main(temp_min = 7.0, temp_max = 17.0, temp = 10.0, feels_like = 8.0, humidity = 50, pressure = 1012, sea_level = 1012, grnd_level = 1012, temp_kf = 0.0),
                    weather = listOf(Weather(id = 600, main = "Snow", description = "snow", icon = "13d")),
                    clouds = Clouds(all = 90),
                    wind = Wind(speed = 10.0, deg = 270, gust = 15.0),
                    visibility = 5000,
                    pop = 0.8,
                    rain = Rain(`3h` = 0.0),
                    sys = Sys(pod = "d")
                )
            )
        )

        // When
        val weeklyForecast = forecastItems.toWeeklyForecast()

        // Then
        assertEquals(2, weeklyForecast.size)

        // Assert first day
        val firstDay = weeklyForecast[0]
        assertEquals("Wednesday", firstDay.day)
        assertEquals("15 Mar", firstDay.date)
        assertEquals("https://openweathermap.org/img/wn/10d@4x.png", firstDay.iconUrl)
        assertEquals("light rain", firstDay.weather)
        assertEquals(18, firstDay.maxTemp)
        assertEquals(5, firstDay.minTemp)

        // Assert second day
        val secondDay = weeklyForecast[1]
        assertEquals("Thursday", secondDay.day)
        assertEquals("16 Mar", secondDay.date)
        assertEquals("https://openweathermap.org/img/wn/13d@4x.png", secondDay.iconUrl)
        assertEquals("snow", secondDay.weather)
        assertEquals(17, secondDay.maxTemp)
        assertEquals(7, secondDay.minTemp)
    }
}