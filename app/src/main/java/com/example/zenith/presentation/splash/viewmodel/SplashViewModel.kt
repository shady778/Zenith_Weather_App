package com.example.zenith.presentation.splash.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SplashState {
    object Idle : SplashState()
    object RequestingPermissions : SplashState()
    object CheckingLocationSettings : SplashState()
    object LoadingData : SplashState()
    object Ready : SplashState()
    data class Error(val message: String) : SplashState()
}

class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow<SplashState>(SplashState.Idle)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    fun updateState(newState: SplashState) {
        _state.value = newState
    }
    
    fun onPermissionsGranted() {
        _state.value = SplashState.CheckingLocationSettings
    }
    
    fun onLocationEnabled() {
        _state.value = SplashState.LoadingData
    }
}
