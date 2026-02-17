package com.anahjanes.feature_weather.week.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.components.ErrorScreen
import com.anahjanes.feature_weather.components.ProgressScreen
import com.anahjanes.feature_weather.components.rememberLocationPermissionHandler
import com.anahjanes.feature_weather.components.PermissionFallback
import com.anahjanes.feature_weather.week.ErrorWeek
import com.anahjanes.feature_weather.week.WeekUiState
import com.anahjanes.feature_weather.week.WeekViewModel
import com.anahjanes.feature_weather.week.model.WeekUiModel

@Composable
fun WeekScreen(
    viewModel: WeekViewModel = hiltViewModel(),
    onOpenCity: () -> Unit,
) {

    WeekScreenContent(
        viewModel = viewModel, onOpenCity
    )

}


@Composable
fun WeekScreenContent(viewModel: WeekViewModel = hiltViewModel(), onOpenCity: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadWeek()
    }
    val state = viewModel.uiState.collectAsState().value
    val locationPermission = rememberLocationPermissionHandler (
        {viewModel.loadWeek()},{}
        )
    when (state) {
        WeekUiState.Idle,
        WeekUiState.Loading,
            -> ProgressScreen()

        is WeekUiState.Error -> {
            when (state.message) {
                ErrorWeek.NO_CITY_SELECTED -> {
                    PermissionFallback(
                        onOpenCity = onOpenCity,
                        onRequestPermissionAgain = {
                            locationPermission.requestPermission()
                        }
                    )
                }

                ErrorWeek.LOAD_WEEK_ERROR -> ErrorScreen { viewModel.loadWeek() }
            }

        }

        is WeekUiState.Success -> {
            WeekContent(state.items, state.city)
        }
    }
}

@Composable
fun WeekContent(
    weeklyForecasts: List<WeekUiModel>,
    city: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            Text(
                text = city,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(weeklyForecasts) { forecast ->
                WeeklyForecastCard(forecast)
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
    WeekContent(weeklyForecasts, "Barcelona")
}

@Preview
@Composable
fun WeeklyForecastItemPreview() {
    WeeklyForecastCard(forecast = weeklyForecasts.first())
}
