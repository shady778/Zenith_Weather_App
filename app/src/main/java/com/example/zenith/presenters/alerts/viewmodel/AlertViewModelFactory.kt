package com.example.zenith.presenters.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zenith.data.repo.AlertRepository
import com.example.zenith.presenters.alerts.logic.AlertScheduler
import kotlin.jvm.java

class AlertViewModelFactory(
    private val repository: AlertRepository,
    private val scheduler: AlertScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(repository, scheduler) as T
        }
        throw kotlin.IllegalArgumentException("Unknown ViewModel class")
    }
}