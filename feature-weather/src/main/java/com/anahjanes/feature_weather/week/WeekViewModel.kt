package com.anahjanes.feature_weather.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.feature_weather.week.model.WeekUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeekUiState>(WeekUiState.Idle)
    val uiState: StateFlow<WeekUiState> = _uiState.asStateFlow()

    fun loadWeek() {
        viewModelScope.launch {
            _uiState.value = WeekUiState.Loading

            val city = repository.observeSelectedCity().first()

            if (city == null) {
                _uiState.value = WeekUiState.Error(ErrorWeek.NO_CITY_SELECTED)

                return@launch
            }

            when (val result = repository.getWeek()) {
                is AppResult.Success -> {
                    val weeklyList = result.data.toWeeklyForecast()
                    _uiState.value = WeekUiState.Success(
                        city = city.name,
                        items = weeklyList
                    )
                }

                is AppResult.Error -> {
                    _uiState.value = WeekUiState.Error(ErrorWeek.LOAD_WEEK_ERROR)
                }
            }
        }
    }
}
enum class ErrorWeek {
    NO_CITY_SELECTED,
    LOAD_WEEK_ERROR
}

sealed interface WeekUiState {
    data object Idle : WeekUiState
    data object Loading : WeekUiState
    data class Success(val items: List<WeekUiModel>, val city: String) : WeekUiState
    data class Error(val message: ErrorWeek) : WeekUiState
}