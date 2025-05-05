package com.sios.tech.plantreminderapp.data.repository

import com.sios.tech.plantreminderapp.data.remote.WeatherApi
import com.sios.tech.plantreminderapp.domain.model.Weather
import com.sios.tech.plantreminderapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val apiKey: String
) : WeatherRepository {
    
    override suspend fun getWeatherForCity(city: String): Result<Weather> {
        return try {
            val response = api.getCurrentWeather(apiKey, city)
            Result.success(
                Weather(
                    location = "${response.location.name}, ${response.location.country}",
                    temperature = response.current.temp_c,
                    condition = response.current.condition.text,
                    humidity = response.current.humidity,
                    windSpeed = response.current.wind_kph,
                    iconUrl = "https:${response.current.condition.icon}"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
