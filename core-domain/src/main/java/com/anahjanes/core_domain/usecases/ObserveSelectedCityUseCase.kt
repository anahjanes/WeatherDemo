package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedCityUseCase @Inject constructor(
    private val repo: WeatherRepository
) {
    operator fun invoke(): Flow<SelectedCity?> = repo.observeSelectedCity()
}