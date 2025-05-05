package com.sios.tech.plantreminderapp.di

import com.sios.tech.plantreminderapp.data.remote.WeatherApi
import com.sios.tech.plantreminderapp.data.repository.WeatherRepositoryImpl
import com.sios.tech.plantreminderapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(httpClient: OkHttpClient): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApiKey(): String {
        // Replace with your actual API key from WeatherAPI.com
        return "b82843aa84af4347bb480822250505"
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi, apiKey: String): WeatherRepository {
        return WeatherRepositoryImpl(api, apiKey)
    }
}
