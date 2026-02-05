package com.anahjanes.feature_weather.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.components.ErrorScreen
import com.anahjanes.feature_weather.components.ProgressScreen
import com.anahjanes.feature_weather.model.HomeWeatherUiModel

@Composable
fun HomeScreen(
    onOpenCity: () -> Unit={},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }


    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        val granted = resultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                resultMap[Manifest.permission.ACCESS_FINE_LOCATION] == true

        hasLocationPermission = granted
        permissionDenied = !granted

        if (granted) {
            viewModel.loadWeather()
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            viewModel.loadWeather()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    when (uiState) {
        is HomeUiState.Success -> WeatherSuccessScreen(weather = uiState.weather)
        is HomeUiState.Loading -> ProgressScreen()
        is HomeUiState.Error ->ErrorScreen(uiState.message)
            /*ErrorScreen(
            message = uiState.message,
            onRetry = {
                if (hasLocationPermission) viewModel.loadWeather()
                else permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onChooseCity = onOpenCity*/
        else -> {

            if (permissionDenied) {
                PermissionFallback(onOpenCity = onOpenCity) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            } else {
                ProgressScreen()
            }
        }
    }
}

@Composable
fun WeatherSuccessScreen(
    weather: HomeWeatherUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.city,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = weather.dateText,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
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

@Composable
fun CurrentWeatherCard(
    temperature: String,
    condition: String,
    feelsLike: String,
    iconUrl: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (iconUrl != null) {
                    coil.compose.AsyncImage(
                        model = iconUrl,
                        contentDescription = condition,
                        modifier = Modifier.size(84.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = temperature,
                fontSize = 86.sp,
                color = Color.White,
                fontWeight = FontWeight.Light
            )

            Text(
                text = condition,
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = feelsLike,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
fun WeatherDetailsGrid(
    tempMax: String,
    tempMin: String,
    clouds: String,
    wind: String,
    humidity: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_temp,
                label = stringResource(id = R.string.weather_detail_temp_hl),
                value = "$tempMax / $tempMin",
                iconBackgroundColor = Color(0xFFFFDDC1)
            )
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_cloud,
                label = stringResource(id = R.string.weather_detail_clouds),
                value = clouds,
                iconBackgroundColor = Color(0xFFC9E8FF)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_wind,
                label = stringResource(id = R.string.weather_detail_wind),
                value = wind,
                iconBackgroundColor = Color(0xFFC8F7E8)
            )
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_humidity,
                label = stringResource(id = R.string.weather_detail_humidity),
                value = humidity,
                iconBackgroundColor = Color(0xFFE1DFFF)
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    value: String,
    iconBackgroundColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = iconBackgroundColor, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WeatherSuccessScreen(
        weather = HomeWeatherUiModel(
            city = "Barcelona",
            dateText = stringResource(id = R.string.today),
            temperature = "25°C",
            condition = "Sunny",
            feelsLike = stringResource(id = R.string.feels_like, "24°C"),
            iconUrl = null,
            tempMax = "28°C",
            tempMin = "22°C",
            clouds = "75%",
            wind = "5 km/h",
            humidity = "60%"
        )
    )
}
