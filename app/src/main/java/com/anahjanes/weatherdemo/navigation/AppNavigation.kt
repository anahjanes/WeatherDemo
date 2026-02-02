package com.anahjanes.weatherdemo.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.anahjanes.feature_weather.city.ui.CityScreen
import com.anahjanes.feature_weather.home.ui.HomeScreen
import com.anahjanes.feature_weather.navigation.City
import com.anahjanes.feature_weather.navigation.Home
import com.anahjanes.feature_weather.navigation.WeatherRoot
import com.anahjanes.feature_weather.navigation.WeatherWeek
import com.anahjanes.feature_weather.week.ui.WeekScreen

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(Weather)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Weather> {
                WeatherRoot()
            }

        }
    )
}