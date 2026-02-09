package com.anahjanes.weatherdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WeatherLightColors = lightColorScheme(
    primary = Color(0xFF4A90E2),
    surface = Color.White,
    background = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSurface = Color(0xFF111111),
    onBackground = Color(0xFF111111),
)

private val WeatherDarkColors = darkColorScheme(
    primary = Color(0xFF4A90E2),
    surface = Color(0xFF1C1C1C),
    background = Color(0xFF121212),
    onPrimary = Color.White,
    onSurface = Color(0xFFEAEAEA),
    onBackground = Color(0xFFEAEAEA),
)

@Composable
fun WeatherTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) WeatherDarkColors else WeatherLightColors,
        content = content
    )
}