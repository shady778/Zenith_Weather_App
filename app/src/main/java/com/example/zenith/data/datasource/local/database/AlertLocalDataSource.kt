package com.example.zenith.data.datasource.local.database

import kotlinx.coroutines.flow.Flow

class AlertLocalDataSource(private val alertDao: AlertDao) {
    val allAlerts: Flow<List<AlertEntity>> = alertDao.getAllAlerts()

    suspend fun insertAlert(alert: AlertEntity) {
        alertDao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        alertDao.deleteAlert(alert)
    }

    suspend fun getAlertById(id: String): AlertEntity? {
        return alertDao.getAlertById(id)
    }
}