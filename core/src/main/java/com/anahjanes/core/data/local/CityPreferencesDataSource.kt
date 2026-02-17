package com.anahjanes.core.data.local

import kotlinx.coroutines.flow.Flow

interface CityPreferencesDataSource {
    val selectedCityFlow: Flow<SelectedCity?>
    suspend fun saveCity(city: SelectedCity)
    suspend fun clear()
     suspend fun getSelectedCity(): SelectedCity?
}