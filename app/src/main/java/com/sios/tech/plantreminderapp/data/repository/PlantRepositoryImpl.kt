package com.sios.tech.plantreminderapp.data.repository

import com.sios.tech.plantreminderapp.data.local.PlantDao
import com.sios.tech.plantreminderapp.data.local.PlantEntity
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val dao: PlantDao
) : PlantRepository {
    override fun getAllPlants(): Flow<List<Plant>> {
        return dao.getAllPlants().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getPlantById(id: Long): Plant? {
        return dao.getPlantById(id)?.toDomainModel()
    }

    override suspend fun insertPlant(plant: Plant): Long {
        return dao.insertPlant(plant.toEntity())
    }

    override suspend fun updatePlant(plant: Plant) {
        dao.updatePlant(plant.toEntity())
    }

    override suspend fun deletePlant(plant: Plant) {
        dao.deletePlant(plant.toEntity())
    }

    private fun PlantEntity.toDomainModel(): Plant {
        return Plant(
            id = id,
            name = name,
            wateringSchedule = wateringSchedule,
            notes = notes,
            lastWatered = lastWatered
        )
    }

    private fun Plant.toEntity(): PlantEntity {
        return PlantEntity(
            id = id,
            name = name,
            wateringSchedule = wateringSchedule,
            notes = notes,
            lastWatered = lastWatered
        )
    }
}
