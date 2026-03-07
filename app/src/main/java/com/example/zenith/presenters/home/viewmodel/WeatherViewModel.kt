package com.example.zenith.presenters.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.repo.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null
)

class WeatherViewModel(val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getWeatherData()
                .catch { exception ->
                    val errorMsg = exception.localizedMessage ?: "Location or Network error"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    _errorEvents.emit(errorMsg)
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { data ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    weatherData = data,
                                    errorMessage = null
                                )
                            }
                        },
                        onFailure = { exception ->
                            val errorMsg = exception.localizedMessage ?: "Failed to fetch weather"
                            _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                            _errorEvents.emit(errorMsg)
                        }
                    )
                }
        }
    }

    fun fetchWeatherForLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getWeatherDataForLocation(lat, lon)
                .catch { exception ->
                    val errorMsg = exception.localizedMessage ?: "Network error"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { data ->
                            _uiState.update { it.copy(isLoading = false, weatherData = data) }
                        },
                        onFailure = { exception ->
                            _uiState.update { it.copy(isLoading = false, errorMessage = exception.localizedMessage) }
                        }
                    )
                }
        }
    }
}
