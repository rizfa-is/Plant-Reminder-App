package com.sios.tech.plantreminderapp.presentation.plants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.domain.model.Weather
import com.sios.tech.plantreminderapp.domain.repository.WeatherRepository
import com.sios.tech.plantreminderapp.domain.usecase.AddPlantUseCase
import com.sios.tech.plantreminderapp.domain.usecase.DeletePlantUseCase
import com.sios.tech.plantreminderapp.domain.usecase.GetPlantsUseCase
import com.sios.tech.plantreminderapp.domain.usecase.UpdatePlantUseCase
import com.sios.tech.plantreminderapp.notification.PlantAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val addPlantUseCase: AddPlantUseCase,
    private val getPlantsUseCase: GetPlantsUseCase,
    private val updatePlantUseCase: UpdatePlantUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
    private val alarmScheduler: PlantAlarmScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val fusedLocationClient: FusedLocationProviderClient
        get() = LocationServices.getFusedLocationProviderClient(context)

    private val _state = MutableStateFlow(PlantsState())
    val state = _state.asStateFlow()

    private val _currentWeather = MutableStateFlow<Result<Weather>?>(null)
    val currentWeather = _currentWeather.asStateFlow()

    // Create location request
    private val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 1000L // 1 second
        fastestInterval = 500L // 500 milliseconds
        isWaitForAccurateLocation = false
    }

    // Create location callback
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                viewModelScope.launch {
                    // Get weather for the new location
                    weatherRepository.getWeatherForLocation(
                        lat = location.latitude,
                        lon = location.longitude
                    ).let { weatherResult ->
                        _currentWeather.value = weatherResult
                    }
                }
            }
        }
    }


    init {
        viewModelScope.launch {
            // Get current location and fetch weather
            getCurrentLocation()

            // Collect plants
            getPlantsUseCase().collect { plants ->
                _state.update { it.copy(
                    plants = plants,
                    isLoading = false
                ) }
            }
        }
    }

    private val _locationState = MutableLiveData<Result<Location>?>(null)

    private fun getCurrentLocation() {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _locationState.value = Result.failure(Exception("Location permission not granted"))
                return@launch
            }

            try {
                // Request location updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null // Looper
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _locationState.value = Result.failure(e)
                _currentWeather.value = Result.failure(e)
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
            is PlantsEvent.SuccessGetLocation -> {
                fusedLocationClient.removeLocationUpdates(locationCallback)
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

    data object SuccessGetLocation: PlantsEvent()
}
