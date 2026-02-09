package com.anahjanes.feature_weather.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.components.ErrorScreen
import com.anahjanes.feature_weather.components.ProgressScreen
import com.anahjanes.feature_weather.components.rememberLocationPermissionHandler
import com.anahjanes.feature_weather.home.HomeUiState
import com.anahjanes.feature_weather.home.HomeViewModel
import com.anahjanes.feature_weather.components.PermissionFallback
import com.anahjanes.feature_weather.home.model.HomeUiModel
import com.anahjanes.feature_weather.ui.theme.WeatherTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenCity: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedCity by viewModel.selectedCity.collectAsState()

    WeatherTheme {
        HomeScreenContent(
            uiState = uiState,
            onOpenCity = onOpenCity,
            loadWeather = { viewModel.loadWeather() },
            selectedCity = selectedCity
        )
    }
}
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onOpenCity: () -> Unit,
    loadWeather: () -> Unit,
    selectedCity: SelectedCity?,
) {
    val locationPermission = rememberLocationPermissionHandler {
        loadWeather()
    }

    LaunchedEffect(selectedCity) {
        if (selectedCity == null) {
            locationPermission.requestPermission()
        } else {
            loadWeather()
        }
    }

    when (uiState) {
        is HomeUiState.Success -> WeatherSuccessScreen(weather = uiState.weather)
        is HomeUiState.Loading -> ProgressScreen()
        is HomeUiState.Error -> ErrorScreen { loadWeather() }
        else -> {
            if (selectedCity == null && locationPermission.permissionDenied) {
                PermissionFallback(
                    onOpenCity = onOpenCity,
                    onRequestPermissionAgain = { locationPermission.requestPermission() }
                )
            } else {
                ProgressScreen()
            }
        }
    }
}

@Composable
fun WeatherSuccessScreen(
    weather: HomeUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            color = MaterialTheme.colorScheme.onBackground,
            text = weather.city,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = weather.dateText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        CurrentWeatherCard(
            temperature = weather.temperature,
            condition = weather.condition,
            feelsLike = weather.feelsLike,
            iconUrl = weather.iconUrl
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherDetailsGrid(
            tempMax = weather.tempMax,
            tempMin = weather.tempMin,
            clouds = weather.clouds,
            wind = weather.wind,
            humidity = weather.humidity
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WeatherTheme() {
        WeatherSuccessScreen(
            weather = HomeUiModel(
                city = "Barcelona",
                dateText = stringResource(id = R.string.today),
                temperature = "25°C",
                condition = "Sunny",
                feelsLike = "10",
                iconUrl = null,
                tempMax = "28°C",
                tempMin = "22°C",
                clouds = "75%",
                wind = "5 km/h",
                humidity = "60%"
            )

        )
    }
}
