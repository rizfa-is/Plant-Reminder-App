package com.sios.tech.plantreminderapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sios.tech.plantreminderapp.domain.model.WeatherState
import com.sios.tech.plantreminderapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun getWeatherForCity(city: String) {
        viewModelScope.launch {
            _state.value = WeatherState(isLoading = true)
            repository.getWeatherForCity(city)
                .onSuccess { weather ->
                    _state.value = WeatherState(weather = weather)
                }
                .onFailure { exception ->
                    _state.value = WeatherState(error = exception.message ?: "Unknown error occurred")
                }
        }
    }
}
