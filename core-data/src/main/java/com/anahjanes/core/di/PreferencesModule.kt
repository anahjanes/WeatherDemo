package com.anahjanes.core.di

import com.anahjanes.core.data.local.CityPreferences
import com.anahjanes.core.data.local.CityPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindCityPreferencesDataSource(
        impl: CityPreferences
    ): CityPreferencesDataSource
}