package com.anahjanes.core_domain.usecases


import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetSelectedCityUseCase @Inject constructor(
    private val repo: WeatherRepository
) {
    suspend operator fun invoke(): SelectedCity? = repo.getSelectedCity()
}