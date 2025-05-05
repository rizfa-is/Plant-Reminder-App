package com.sios.tech.plantreminderapp.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.usecase.AddPlantUseCase
import com.sios.tech.plantreminderapp.domain.usecase.DeletePlantUseCase
import com.sios.tech.plantreminderapp.domain.usecase.GetPlantsUseCase
import com.sios.tech.plantreminderapp.domain.usecase.UpdatePlantUseCase
import com.sios.tech.plantreminderapp.notification.PlantAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val addPlantUseCase: AddPlantUseCase,
    private val getPlantsUseCase: GetPlantsUseCase,
    private val updatePlantUseCase: UpdatePlantUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
    private val alarmScheduler: PlantAlarmScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(PlantsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getPlantsUseCase().collect { plants ->
                _state.update { it.copy(
                    plants = plants,
                    isLoading = false
                ) }
            }
        }
    }

    fun onEvent(event: PlantsEvent) {
        when (event) {
            is PlantsEvent.AddPlant -> {
                viewModelScope.launch {
                    val plant = Plant(
                        name = event.name,
                        wateringSchedule = event.wateringSchedule,
                        notes = event.notes
                    )
                    addPlantUseCase(plant)
                    alarmScheduler.scheduleWateringReminder(plant)
                }
            }
            is PlantsEvent.UpdatePlant -> {
                viewModelScope.launch {
                    val updatedPlant = event.plant.copy(
                        name = event.name,
                        wateringSchedule = event.wateringSchedule,
                        notes = event.notes
                    )
                    updatePlantUseCase(updatedPlant)
                    alarmScheduler.cancelWateringReminder(event.plant.id)
                    alarmScheduler.scheduleWateringReminder(updatedPlant)
                }
            }
            is PlantsEvent.DeletePlant -> {
                viewModelScope.launch {
                    deletePlantUseCase(event.plant)
                    alarmScheduler.cancelWateringReminder(event.plant.id)
                }
            }
        }
    }
}

data class PlantsState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true
)

sealed class PlantsEvent {
    data class AddPlant(
        val name: String,
        val wateringSchedule: Date,
        val notes: String
    ) : PlantsEvent()

    data class UpdatePlant(
        val plant: Plant,
        val name: String,
        val wateringSchedule: Date,
        val notes: String
    ) : PlantsEvent()

    data class DeletePlant(
        val plant: Plant
    ) : PlantsEvent()
}
