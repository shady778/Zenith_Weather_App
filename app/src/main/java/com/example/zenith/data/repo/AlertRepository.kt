package com.example.zenith.data.repo

import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.AlertLocalDataSource
import kotlinx.coroutines.flow.Flow

class AlertRepository(private val localDataSource: AlertLocalDataSource) {
    val allAlerts: Flow<List<AlertEntity>> = localDataSource.allAlerts

    suspend fun insertAlert(alert: AlertEntity) {
        localDataSource.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
    }

    suspend fun getAlertById(id: String): AlertEntity? {
        return localDataSource.getAlertById(id)
    }
}
