package com.example.zenith.data.datasource.local
import kotlinx.coroutines.flow.Flow
class FavoriteLocalDataSource(private val favoriteCityDao: FavoriteCityDao) {

    val allFavorites: Flow<List<FavoriteCityEntity>> = favoriteCityDao.getAllFavorites()

    suspend fun insertCity(city: FavoriteCityEntity) {
        favoriteCityDao.insertCity(city)
    }

    suspend fun deleteCity(city: FavoriteCityEntity) {
        favoriteCityDao.deleteCity(city)
    }
}