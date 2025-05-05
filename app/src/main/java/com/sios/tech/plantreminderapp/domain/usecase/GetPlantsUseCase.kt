package com.sios.tech.plantreminderapp.domain.usecase

import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlantsUseCase @Inject constructor(
    private val repository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> = repository.getAllPlants()
}
