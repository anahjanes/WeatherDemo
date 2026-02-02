package com.anahjanes.core.data.remote

import com.anahjanes.core.data.remote.dto.CurrentWeatherDto
import com.anahjanes.core.data.remote.dto.GeoCityDto
import com.anahjanes.core.data.remote.dto.WeekWeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = WeatherApiConfig.UNITS,
        @Query("lang") lang: String = WeatherApiConfig.LANG,

    ): CurrentWeatherDto


    @GET("data/2.5/forecast")
    suspend fun getSevenDayForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = WeatherApiConfig.UNITS,
        @Query("lang") lang: String = WeatherApiConfig.LANG
    ): WeekWeatherDto


    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<GeoCityDto>
}