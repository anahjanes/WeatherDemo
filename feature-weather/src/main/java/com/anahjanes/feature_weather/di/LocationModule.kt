package com.anahjanes.feature_weather.di

import com.anahjanes.feature_weather.location.FusedLocationDataSource
import com.anahjanes.feature_weather.location.LocationDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindLocationDataSource(
        impl: FusedLocationDataSource
    ): LocationDataSource
}