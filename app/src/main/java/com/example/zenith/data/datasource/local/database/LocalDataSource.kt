package com.example.zenith.data.datasource.local.database
import kotlinx.coroutines.flow.Flow
class LocalDataSource(
    private val favoriteCityDao: FavoriteCityDao,
    private val alertDao: AlertDao
) {

    val allFavorites: Flow<List<FavoriteCityEntity>> = favoriteCityDao.getAllFavorites()

    suspend fun insertCity(city: FavoriteCityEntity) {
        favoriteCityDao.insertCity(city)
    }

    suspend fun deleteCity(city: FavoriteCityEntity) {
        favoriteCityDao.deleteCity(city)
    }
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