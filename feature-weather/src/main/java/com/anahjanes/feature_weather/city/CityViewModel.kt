package com.anahjanes.feature_weather.city

import androidx.lifecycle.ViewModel
import com.anahjanes.core.data.WeatherRepository
import com.anahjanes.core.data.WeatherRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(val repository: WeatherRepository) : ViewModel() {
    val currentCity = repository.observeSelectedCity()


}