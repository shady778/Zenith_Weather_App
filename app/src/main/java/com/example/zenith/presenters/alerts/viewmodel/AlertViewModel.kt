package com.example.zenith.presenters.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presenters.alerts.logic.AlertScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertViewModel(
    private val repository: WeatherRepository,
    private val scheduler: AlertScheduler
) : ViewModel() {

    val alerts: StateFlow<List<AlertEntity>> = repository.allAlerts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.insertAlert(alert)
            if (alert.isEnabled) {
                scheduler.schedule(alert)
            }
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            scheduler.cancel(alert)
        }
    }

    fun toggleAlert(alert: AlertEntity) {
        viewModelScope.launch {
            val updatedAlert = alert.copy(isEnabled = !alert.isEnabled)
            repository.insertAlert(updatedAlert)
            if (updatedAlert.isEnabled) {
                scheduler.schedule(updatedAlert)
            } else {
                scheduler.cancel(updatedAlert)
            }
        }
    }
}