package com.sios.tech.plantreminderapp.domain.repository

import com.sios.tech.plantreminderapp.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getAllPlants(): Flow<List<Plant>>
    suspend fun getPlantById(id: Long): Plant?
    suspend fun insertPlant(plant: Plant): Long
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
}
