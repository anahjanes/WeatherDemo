package com.anahjanes.feature_weather.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahjanes.core_domain.repository.WeatherRepository
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.usecases.GetSelectedCityUseCase
import com.anahjanes.core_domain.usecases.ObserveSelectedCityUseCase
import com.anahjanes.core_domain.usecases.SaveCityUseCase
import com.anahjanes.core_domain.usecases.SearchCitiesUseCase
import com.anahjanes.feature_weather.city.model.CityUiModel
import com.anahjanes.feature_weather.city.model.toUiItem
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
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val observeSelectedCityUseCase: ObserveSelectedCityUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val locationDataSource: LocationDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        observeCurrentCity()
    }

    private fun observeCurrentCity() {
        viewModelScope.launch {
            observeSelectedCityUseCase()
                .collect { city ->
                    _uiState.update { it.copy(currentCity = city?.name?.takeIf { n -> n.isNotBlank() }) }
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
            saveCityUseCase(
                SelectedCity(
                    name = "",
                    lat = location.lat,
                    lon = location.lon
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


    fun onCitySelected(city: CityUiModel) {
        viewModelScope.launch {

            val selectedCity = SelectedCity(name = buildCityName(city), lat = city.lat, lon = city.lon)

            saveCityUseCase(selectedCity)

            _uiState.update {
                it.copy(
                    searchQuery = "",
                    citySelected = true
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(citySelected = false, results = emptyList()) }
    }

    private fun buildCityName(city: CityUiModel): String {
        return listOfNotNull(
            city.name,
            city.country
        ).joinToString(", ")
    }


    fun searchCity() {
        val query = _uiState.value.searchQuery
        if (query.length < 3) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }


            when (val result = searchCitiesUseCase(query)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = result.data.map { city -> city.toUiItem() }
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
    val results: List<CityUiModel> = emptyList(),
    val error: String? = null,
    val currentCity: String? = null,
    val citySelected: Boolean = false,
) {
    val isSearchEnabled: Boolean
        get() = searchQuery.length >= 3 && !isSearching
}