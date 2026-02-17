package com.anahjanes.feature_weather.city.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.city.CityUiState
import com.anahjanes.feature_weather.city.CityViewModel
import com.anahjanes.feature_weather.city.model.CityUiModel
import com.anahjanes.feature_weather.components.rememberLocationPermissionHandler

@Composable
fun CityScreen(
    viewModel: CityViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.citySelected) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
            viewModel.onNavigationHandled()
        }
    }
        CityScreenContent(
            uiState = uiState,
            onSearchQueryChange = viewModel::onSearchQueryChanged,
            onSearchClick = viewModel::searchCity,
            onCitySelected = viewModel::onCitySelected,
            onUseCurrentLocation = viewModel::onUseCurrentLocation
        )
    }


@Composable
fun CityScreenContent(
    uiState: CityUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (CityUiModel) -> Unit,
    onUseCurrentLocation: () -> Unit,
) {

    val locationPermission = rememberLocationPermissionHandler (
        onPermissionGranted = { onUseCurrentLocation() },
        onPermissionDenied = {  })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.select_city_title),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(16.dp))

        uiState.currentCity?.let {
            CurrentCityCard(city = it)
            Spacer(Modifier.height(24.dp))
        }

        SearchCityCard(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearchClick = onSearchClick,
            isSearchEnabled = uiState.isSearchEnabled,
            isSearching = uiState.isSearching
        )

        Spacer(Modifier.height(16.dp))

        UseLocationButton(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            locationPermission.requestPermission()
        }

        if (uiState.isSearching) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.results) { city ->
                    CityRow(city = city) {
                        onCitySelected(city)
                    }
                }
            }
        }

        uiState.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = stringResource(R.string.no_connection_message), color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityScreenPreview() {

        CityScreenContent(
            CityUiState(currentCity = "Barcelona"),
            onSearchQueryChange = { },
            onSearchClick = { },
            onCitySelected = { },
            onUseCurrentLocation = { }

        )

}
