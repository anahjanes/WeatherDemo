package com.anahjanes.core.di

import com.anahjanes.core.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.anahjanes.core.BuildConfig


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/"

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val urlWithKey = request.url.newBuilder()
            .addQueryParameter("appid", BuildConfig.OPEN_WEATHER_API_KEY)
            .build()

        val newRequest = request.newBuilder()
            .url(urlWithKey)
            .build()

        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)

        // Logging solo en debug
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
   internal fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}