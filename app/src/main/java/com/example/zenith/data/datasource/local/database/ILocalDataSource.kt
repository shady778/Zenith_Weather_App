package com.example.zenith.data.datasource.local.database

import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
        val allFavorites: Flow<List<FavoriteCityEntity>>
        suspend fun insertCity(city: FavoriteCityEntity)
        suspend fun deleteCity(city: FavoriteCityEntity)
        val allAlerts: Flow<List<AlertEntity>>
        suspend fun insertAlert(alert: AlertEntity)
        suspend fun deleteAlert(alert: AlertEntity)
        suspend fun getAlertById(id: String): AlertEntity?
        val weatherCache: Flow<WeatherEntity?>
        suspend fun insertWeatherCache(cache: WeatherEntity)
        suspend fun clearWeatherCache()
    }
