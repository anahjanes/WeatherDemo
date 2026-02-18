package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CityResult
import com.anahjanes.core_domain.repository.WeatherRepository
import javax.inject.Inject


class SearchCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 10): AppResult<List<CityResult>> {
        if (query.isBlank()) return AppResult.Success(emptyList())
        return repository.searchCities(query, limit)
    }
}