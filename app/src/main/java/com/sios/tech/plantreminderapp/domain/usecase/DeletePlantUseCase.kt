package com.sios.tech.plantreminderapp.domain.usecase

import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.repository.PlantRepository
import javax.inject.Inject

class DeletePlantUseCase @Inject constructor(
    private val repository: PlantRepository
) {
    suspend operator fun invoke(plant: Plant) = repository.deletePlant(plant)
}
