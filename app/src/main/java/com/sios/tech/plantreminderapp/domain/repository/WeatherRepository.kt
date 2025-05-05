package com.sios.tech.plantreminderapp.domain.repository

import com.sios.tech.plantreminderapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeatherForCity(city: String): Result<Weather>
}
