package com.anahjanes.feature_weather.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.feature_weather.location.LocationDataSource
import com.anahjanes.feature_weather.home.model.HomeUiModel
import com.anahjanes.feature_weather.home.model.toHomeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationDataSource: LocationDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val selectedCity = weatherRepository.observeSelectedCity()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val savedCity = selectedCity.value

            val result = if (savedCity != null) {
                weatherRepository.getTodayByCoords(savedCity.lat, savedCity.lon)
            } else {
                val location = locationDataSource.getCurrentLocation()
                    .getOrElse {
                        _uiState.value = HomeUiState.Error
                        return@launch
                    }
                weatherRepository.getTodayByCoords(location.lat, location.lon)
            }

            _uiState.value = when (result) {
                is AppResult.Success -> HomeUiState.Success(result.data.toHomeUiModel())
                is AppResult.Error -> HomeUiState.Error
            }
        }
    }
}


sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success(val weather: HomeUiModel) : HomeUiState
    data object Error : HomeUiState
}