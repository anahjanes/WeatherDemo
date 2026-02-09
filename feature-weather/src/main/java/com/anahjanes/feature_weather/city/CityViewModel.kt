package com.anahjanes.feature_weather.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.local.SelectedCity
import com.anahjanes.core.data.remote.AppResult
import com.anahjanes.core.data.remote.dto.GeoCityDto
import com.anahjanes.feature_weather.location.LocationDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationDataSource: LocationDataSource,

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        observeCurrentCity()
    }

    private fun observeCurrentCity() {
        viewModelScope.launch {
            repository.observeSelectedCity().collect { city ->
                _uiState.update {
                    it.copy(currentCity = city?.name)
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                error = null
            )
        }
    }

    fun onUseCurrentLocation() {
        viewModelScope.launch {
            val location = locationDataSource.getCurrentLocation()
                .getOrElse {

                    return@launch
                }
            //We save city with no name and when it retrieves weather it will be filled
            repository.saveCity(
                SelectedCity(
                    name = "",
                    lat = location.lat,
                    lon = location.lon
                ))
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    citySelected = true
                )
            }
        }
    }


    fun onCitySelected(city: GeoCityDto) {
        viewModelScope.launch {
            repository.saveCity(
                SelectedCity(
                    name = buildCityName(city),
                    lat = city.lat,
                    lon = city.lon
                )
            )
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    citySelected = true
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(citySelected = false) }
    }

    private fun buildCityName(city: GeoCityDto): String {
        return listOfNotNull(
            city.name,
            city.state,
            city.country
        ).joinToString(", ")
    }


    fun searchCity() {
        val query = _uiState.value.searchQuery
        if (query.length < 3) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }

            when (val result = repository.searchCities(query, limit = 10)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = result.data
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            error = result.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}

data class CityUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val results: List<GeoCityDto> = emptyList(),
    val error: String? = null,
    val currentCity: String? = null,
    val citySelected: Boolean = false,
) {
    val isSearchEnabled: Boolean
        get() = searchQuery.length >= 3 && !isSearching
}