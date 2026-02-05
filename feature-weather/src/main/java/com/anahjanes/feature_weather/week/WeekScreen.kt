package com.anahjanes.feature_weather.week

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WeekScreen(viewModel: WeekViewModel = hiltViewModel()) {
    Box {
        Text(text = "Week")
    }
}