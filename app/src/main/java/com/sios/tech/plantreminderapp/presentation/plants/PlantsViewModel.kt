package com.sios.tech.plantreminderapp.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.usecase.AddPlantUseCase
import com.sios.tech.plantreminderapp.domain.usecase.GetPlantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val getPlantsUseCase: GetPlantsUseCase,
    private val addPlantUseCase: AddPlantUseCase
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
}
