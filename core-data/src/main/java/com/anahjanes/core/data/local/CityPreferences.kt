package com.anahjanes.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.anahjanes.core_domain.model.SelectedCity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "city_prefs")

@Singleton
class CityPreferences @Inject constructor(@ApplicationContext private val context: Context):CityPreferencesDataSource {

    private object Keys {
        val NAME = stringPreferencesKey("city_name")
        val LAT = doublePreferencesKey("city_lat")
        val LON = doublePreferencesKey("city_lon")
    }

    override val selectedCityFlow: Flow<SelectedCity?> =
        context.dataStore.data.map { prefs ->
            val name = prefs[Keys.NAME] ?: return@map null
            val lat = prefs[Keys.LAT] ?: return@map null
            val lon = prefs[Keys.LON] ?: return@map null
            SelectedCity(
                name = name,
                lat = lat,
                lon = lon
            )
        }

    override suspend fun saveCity(city: SelectedCity) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = city.name
            prefs[Keys.LAT] = city.lat
            prefs[Keys.LON] = city.lon
        }
    }
    override suspend fun getSelectedCity(): SelectedCity? {
        return selectedCityFlow.first()
    }

    override suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.NAME)
            prefs.remove(Keys.LAT)
            prefs.remove(Keys.LON)
        }
    }
}