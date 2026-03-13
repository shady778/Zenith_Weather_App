package com.example.zenith.data.datasource.local.database
import kotlinx.coroutines.flow.Flow
class LocalDataSource(
    private val favoriteCityDao: FavoriteCityDao,
    private val alertDao: AlertDao,
    private val weatherDao: WeatherDao
) : ILocalDataSource {

    override val allFavorites: Flow<List<FavoriteCityEntity>> = favoriteCityDao.getAllFavorites()

    override suspend fun insertCity(city: FavoriteCityEntity) {
        favoriteCityDao.insertCity(city)
    }

    override suspend fun deleteCity(city: FavoriteCityEntity) {
        favoriteCityDao.deleteCity(city)
    }
    override val allAlerts: Flow<List<AlertEntity>> = alertDao.getAllAlerts()

    override suspend fun insertAlert(alert: AlertEntity) {
        alertDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        alertDao.deleteAlert(alert)
    }

    override suspend fun getAlertById(id: String): AlertEntity? {
        return alertDao.getAlertById(id)
    }
    override val weatherCache: Flow<WeatherEntity?> = weatherDao.getWeatherCache()

    override suspend fun insertWeatherCache(cache: WeatherEntity) {
        weatherDao.insertWeatherCache(cache)
    }

    override suspend fun clearWeatherCache() {
        weatherDao.clearWeatherCache()
    }

}