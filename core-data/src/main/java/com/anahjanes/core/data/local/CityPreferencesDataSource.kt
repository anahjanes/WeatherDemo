package com.anahjanes.core.data.local

import com.anahjanes.core_domain.model.SelectedCity
import kotlinx.coroutines.flow.Flow

interface CityPreferencesDataSource {
    val selectedCityFlow: Flow<SelectedCity?>
    suspend fun saveCity(city: SelectedCity)
    suspend fun clear()
     suspend fun getSelectedCity(): SelectedCity?
}