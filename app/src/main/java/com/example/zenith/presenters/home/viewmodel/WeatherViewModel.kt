package com.example.zenith.presenters.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.repo.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.zenith.data.network.NetworkMonitor

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null,
    val isOnline: Boolean = true,
    val showNoInternetAlert: Boolean = false
)

class WeatherViewModel(
    val repository: WeatherRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    init {
        monitorConnectivity()
        fetchWeather()
    }

    private fun monitorConnectivity() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    fun triggerNoInternetAlert() {
        _uiState.update { it.copy(showNoInternetAlert = true) }
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
