package com.example.zenith.data.repo

import com.example.zenith.data.datasource.local.FavoriteCityEntity
import com.example.zenith.data.datasource.local.FavoriteLocalDataSource
import com.example.zenith.data.datasource.location.LocationProvider
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.mapResponseToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val locationProvider: LocationProvider,
    private val localDataSource: FavoriteLocalDataSource
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeatherData(): Flow<Result<WeatherData>> {
        return locationProvider.fetchLocation().flatMapLatest { coords ->
            flow {
                try {
                    val current = remoteDataSource.getCurrentWeather(coords.first, coords.second)
                    val forecast = remoteDataSource.getForecast(coords.first, coords.second)

                    val mapped = mapResponseToData(current, forecast)
                    emit(Result.success(mapped))
                } catch (e: Exception) {
                    emit(Result.failure(e))
                }
            }

        }
    }

    fun getWeatherDataForLocation(lat: Double, lon: Double): Flow<Result<WeatherData>> {
        return flow {
            try {
                val current = remoteDataSource.getCurrentWeather(lat, lon)
                val forecast = remoteDataSource.getForecast(lat, lon)
                val mapped = mapResponseToData(current, forecast)
                emit(Result.success(mapped))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double) = 
        remoteDataSource.getCurrentWeather(lat, lon)

    val allFavorites: Flow<List<FavoriteCityEntity>> =

        localDataSource.allFavorites

    suspend fun insert(city: FavoriteCityEntity) {
        localDataSource.insertCity(city)
    }

    suspend fun delete(city: FavoriteCityEntity) {
        localDataSource.deleteCity(city)
    }

}