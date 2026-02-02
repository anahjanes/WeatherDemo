package com.anahjanes.feature_weather.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.anahjanes.feature_weather.city.ui.CityScreen
import com.anahjanes.feature_weather.components.WeatherBottomBar
import com.anahjanes.feature_weather.home.ui.HomeScreen
import com.anahjanes.feature_weather.week.ui.WeekScreen

@Composable
fun WeatherRoot() {
    val backStack = rememberNavBackStack(Home)

    Scaffold(
        bottomBar = {
            WeatherBottomBar(
                current = backStack.lastOrNull(),
                onHomeClick = { backStack.clear(); backStack.add(Home) },
                onWeekClick = { backStack.clear(); backStack.add(WeatherWeek) },
                onCityClick = { backStack.clear(); backStack.add(City) },
            )
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(padding),
            entryProvider = entryProvider {
                entry<Home> { HomeScreen{
                    backStack.clear()
                    backStack.add(City)
                } }
                entry<WeatherWeek> { WeekScreen(){
                    backStack.clear()
                    backStack.add(City)
                } }
                entry<City> { CityScreen() {
                    backStack.clear()
                    backStack.add(Home)
                } }
            }
        )
    }
}