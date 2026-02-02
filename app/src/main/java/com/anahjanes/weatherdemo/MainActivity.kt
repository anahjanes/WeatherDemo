package com.anahjanes.weatherdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anahjanes.feature_weather.navigation.WeatherRoot
import com.anahjanes.weatherdemo.navigation.AppNavigation
import com.anahjanes.weatherdemo.ui.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme() {
                AppNavigation()
            }
        }
    }
}
