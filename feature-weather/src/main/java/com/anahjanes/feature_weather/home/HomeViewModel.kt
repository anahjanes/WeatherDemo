package com.anahjanes.feature_weather.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.feature_weather.location.LocationDataSource
import com.anahjanes.feature_weather.model.HomeWeatherUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationDataSource: LocationDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val savedCity = weatherRepository.observeSelectedCity().first()

            val result = if (savedCity != null) {
                weatherRepository.getTodayByCoords(savedCity.lat, savedCity.lon)
            } else {
                val location = locationDataSource.getCurrentLocation()
                    .getOrElse {
                        _uiState.value = HomeUiState.Error("Location unavailable")
                        return@launch
                    }

                weatherRepository.getTodayByCoords(location.lat, location.lon)
            }

            when (result) {
                is AppResult.Success ->
                    _uiState.value = HomeUiState.Success(result.data.toHomeUiModel())

                is AppResult.Error ->
                    _uiState.value = HomeUiState.Error(result.message ?: "Error")
            }
        }
    }
}


sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success(val weather: HomeWeatherUiModel) : HomeUiState
    data class Error(val message: String) : HomeUiState
}