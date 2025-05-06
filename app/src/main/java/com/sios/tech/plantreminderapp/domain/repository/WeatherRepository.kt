package com.sios.tech.plantreminderapp.domain.repository

import com.sios.tech.plantreminderapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeatherForCity(city: String): Result<Weather>
    suspend fun getWeatherForLocation(lat: Double, lon: Double): Result<Weather>
}
