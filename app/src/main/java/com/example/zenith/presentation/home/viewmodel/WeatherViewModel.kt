package com.example.zenith.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.repo.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.zenith.utils.LocationMonitor
import com.example.zenith.utils.NetworkMonitor

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null,
    val isOnline: Boolean = true,
    val isLocationEnabled: Boolean = true,
    val isGpsMode: Boolean = true,
    val showNoInternetAlert: Boolean = false
)

class WeatherViewModel(
    val repository: WeatherRepository,
    private val networkMonitor: NetworkMonitor,
    private val locationMonitor: LocationMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    init {
        monitorConnectivity()
        monitorLocation()
        monitorGpsMode()
        fetchWeather()
    }

    private fun monitorConnectivity() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    private fun monitorLocation() {
        viewModelScope.launch {
            locationMonitor.isLocationEnabled.collect { enabled ->
                val previous = _uiState.value.isLocationEnabled
                _uiState.update { it.copy(isLocationEnabled = enabled) }
                if (enabled && !previous && _uiState.value.isGpsMode) {
                    fetchWeather()
                }
            }
        }
    }

    private fun monitorGpsMode() {
        viewModelScope.launch {
            repository.settingsDataStore.settingsFlow.collect { settings ->
                _uiState.update { it.copy(isGpsMode = settings.locProvider == "GPS") }
            }
        }
    }

    fun dismissNoInternetAlert() {
        _uiState.update { it.copy(showNoInternetAlert = false) }
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
}
