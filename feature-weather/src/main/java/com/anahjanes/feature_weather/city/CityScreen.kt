package com.anahjanes.feature_weather.city

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CityScreen(viewModel: CityViewModel = hiltViewModel()) {
    Box {
        Text(text = "City")
    }
}