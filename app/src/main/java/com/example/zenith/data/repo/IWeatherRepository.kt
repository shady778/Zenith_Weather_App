package com.example.zenith.data.repo

import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun getWeatherData(): Flow<Result<WeatherData>>
    fun getWeatherDataForLocation(lat: Double, lon: Double): Flow<Result<WeatherData>>

    val allFavorites: Flow<List<FavoriteCityEntity>>
    suspend fun insert(city: FavoriteCityEntity)
    suspend fun delete(city: FavoriteCityEntity)

    val allAlerts: Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun deleteAlert(alert: AlertEntity)
    suspend fun getAlertById(id: String): AlertEntity?
}