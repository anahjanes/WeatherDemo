package com.anahjanes.feature_weather.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.feature_weather.location.LocationDataSource
import com.anahjanes.feature_weather.home.model.HomeUiModel
import com.anahjanes.feature_weather.home.model.toHomeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationDataSource: LocationDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val savedCity = weatherRepository.requireSelectedCity()

            if (savedCity != null) {
                loadWeatherForCoords(savedCity.lat, savedCity.lon)
                return@launch
            }

            // No hay ciudad guardada
            if (!locationDataSource.hasLocationPermission()) {
                _events.emit(HomeEvent.RequestLocationPermission)
                return@launch
            }

            loadWeatherFromLocation()
        }
    }

    private suspend fun loadWeatherFromLocation() {
        val location = locationDataSource.getCurrentLocation()
            .getOrElse {
                _uiState.value = HomeUiState.Error
                return
            }

        loadWeatherForCoords(location.lat, location.lon)
    }

    private suspend fun loadWeatherForCoords(lat: Double, lon: Double) {
        val result = weatherRepository.getTodayByCoords(lat, lon)

        _uiState.value = when (result) {
            is AppResult.Success -> HomeUiState.Success(result.data.toHomeUiModel())
            is AppResult.Error -> HomeUiState.Error
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch { loadWeatherFromLocation() }
    }

    fun onPermissionDenied() {
        _uiState.value = HomeUiState.NeedsLocationPermission
    }
}

sealed interface HomeUiState {
    data object Idle : HomeUiState                // Pantalla inicial
    data object Loading : HomeUiState             // Cargando datos
    data class Success(val weather: HomeUiModel) : HomeUiState // Weather cargado
    data object Error : HomeUiState               // Error de carga
    data object NeedsLocationPermission : HomeUiState // Fallback visible si no hay ciudad y usuario negó permiso
}
sealed interface HomeEvent {
    data object RequestLocationPermission : HomeEvent
}
