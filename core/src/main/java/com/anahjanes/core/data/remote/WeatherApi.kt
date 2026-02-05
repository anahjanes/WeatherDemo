package com.anahjanes.core.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // 1) Tiempo actual (hoy) por ciudad
    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): CurrentWeatherDto

    // 2) Tiempo actual (hoy) por coordenadas (útil para "current city")
    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): CurrentWeatherDto

    // 3) Forecast semanal (One Call 3.0) por coordenadas
    // Excluimos minutely/hourly/alerts para ir a lo necesario.
    @GET("data/3.0/onecall")
    suspend fun getWeeklyForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly,alerts",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): OneCallDto


    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<GeoCityDto>
}