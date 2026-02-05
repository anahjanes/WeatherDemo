package com.anahjanes.feature_weather.week

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.components.ProgressScreen
import com.anahjanes.feature_weather.ui.theme.WeatherTheme
import com.anahjanes.feature_weather.week.model.WeekUiModel

@Composable
fun WeekScreen(
    viewModel: WeekViewModel = hiltViewModel(),
) {
    WeatherTheme {
        WeekScreenContent(
            viewModel = viewModel
        )
    }
}


@Composable
fun WeekScreenContent(viewModel: WeekViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadWeek()
    }
    val state = viewModel.uiState.collectAsState().value

    when (state) {
        WeekUiState.Idle,
        WeekUiState.Loading -> ProgressScreen()

        is WeekUiState.Error -> {
            val message = when (state.message) {
                ErrorWeek.NO_CITY_SELECTED -> stringResource(R.string.no_city_selected)
                ErrorWeek.LOAD_WEEK_ERROR -> stringResource(R.string.error_loading_week)
            }
            Text(text = message, color = Color.Red)
        }

        is WeekUiState.Success -> {
            WeekContent(state.items, state.city)
        }
    }
}

@Composable
fun WeekContent(
    weeklyForecasts: List<WeekUiModel>,
    city: String
) {
    Column(modifier = Modifier   .fillMaxSize() .background(MaterialTheme.colorScheme.background) .padding(16.dp),) {

        Text(
            text = stringResource(R.string.weekly_forecast),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = city, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(weeklyForecasts) { forecast ->
                WeeklyForecastCard(forecast)
            }
        }
    }
}


@Composable
fun WeeklyForecastCard(
    forecast: WeekUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🟠 Icono con fondo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color =  Color(0xFFC9E8FF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                forecast.iconUrl?.let {
                    coil.compose.AsyncImage(
                        model = it,
                        contentDescription = forecast.weather,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 📅 Día + descripción
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = forecast.day,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (forecast.date != null) {
                    Text(
                        text = forecast.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = forecast.weather,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 🌡 Temperaturas
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "↑ ${forecast.maxTemp}°",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "↓ ${forecast.minTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}


val weeklyForecasts = listOf(
    WeekUiModel("Today", "OCT 24", null, "Sunny", 72, 58),
    WeekUiModel("Monday", null, null, "Partly Cloudy", 70, 56),
    WeekUiModel("Tuesday", null, null, "Showers", 65, 54),
    WeekUiModel("Wed", null, null, "Stormy", 62, 50),
    WeekUiModel("Thursday", null, null, "Sunny", 68, 52),
    WeekUiModel("Friday", null, null, "Mostly Sunny", 71, 55),
    WeekUiModel("Saturday", null, null, "Cloudy", 69, 57),
)

@Preview
@Composable
fun WeekContentPreview() {
    WeatherTheme {
    WeekContent(weeklyForecasts,"Barcelona")
    }
}

@Preview
@Composable
fun WeeklyForecastItemPreview() {
    WeeklyForecastCard(forecast = weeklyForecasts.first())
}
