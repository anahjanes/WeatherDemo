package com.anahjanes.core_domain.usecases

import com.anahjanes.core_domain.model.AppResult
import com.anahjanes.core_domain.model.CurrentWeather
import com.anahjanes.core_domain.model.SelectedCity
import com.anahjanes.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetTodayWeatherUseCase @Inject constructor(
    private val repo: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): AppResult<CurrentWeather> {
        val selectedCity = repo.getSelectedCity()

        val result = repo.getTodayByCoords(lat = lat, lon = lon)

        if (result is AppResult.Success) {
            val shouldSaveBecauseNoneSelected = selectedCity == null
            val shouldBackfillBlankNameSameCoords =
                selectedCity != null &&
                        selectedCity.lat == lat &&
                        selectedCity.lon == lon &&
                        selectedCity.name.isBlank()

            if (shouldSaveBecauseNoneSelected || shouldBackfillBlankNameSameCoords) {
                repo.saveCity(
                    SelectedCity(
                        name = result.data.cityName,
                        lat = lat,
                        lon = lon
                    )
                )
            }
        }

        return result
    }
}