package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import javax.inject.Inject

class SaveCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: SelectedCity) = repository.saveCity(city)
}