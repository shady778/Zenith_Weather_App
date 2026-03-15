package com.example.zenith.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zenith.utils.NetworkMonitor
import com.example.zenith.data.repo.WeatherRepository

import com.example.zenith.utils.LocationMonitor

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val networkMonitor: NetworkMonitor,
    private val locationMonitor: LocationMonitor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository, networkMonitor, locationMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}