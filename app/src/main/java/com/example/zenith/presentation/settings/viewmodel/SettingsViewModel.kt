package com.example.zenith.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.local.datastore.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsDataStore: SettingsDataStore) : ViewModel() {

    val settingsState: StateFlow<UserSettings> = settingsDataStore.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings("CELSIUS", "MS", "ENGLISH", "GPS", true, false, 0.0, 0.0)
        )

    fun updateSettings(newSettings: UserSettings) {
        viewModelScope.launch {
            settingsDataStore.updateSettings(newSettings)
        }
    }
}