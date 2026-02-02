package com.anahjanes.feature_weather.components

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.navigation.City
import com.anahjanes.feature_weather.navigation.Home
import com.anahjanes.feature_weather.navigation.WeatherWeek

@Composable
fun WeatherBottomBar(
    current: Any?,
    onHomeClick: () -> Unit,
    onWeekClick: () -> Unit,
    onCityClick: () -> Unit,
) {

        NavigationBar {
            NavigationBarItem(
                selected = current is Home,
                onClick = onHomeClick,
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = stringResource(id = R.string.home)
                    )
                },
                label = { Text(stringResource(id = R.string.home)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                selected = current is WeatherWeek,
                onClick = onWeekClick,
                icon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = stringResource(id = R.string.week)
                    )
                },
                label = { Text(stringResource(id = R.string.week)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                selected = current is City,
                onClick = onCityClick,
                icon = {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = stringResource(id = R.string.city)
                    )
                },
                label = { Text(stringResource(id = R.string.city)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }

}