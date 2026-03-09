package com.example.zenith.data.repo

import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.datasource.local.database.FavoriteLocalDataSource
import com.example.zenith.data.datasource.location.LocationProvider
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.mapResponseToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    val remoteDataSource: WeatherRemoteDataSource,
    private val locationProvider: LocationProvider,
    private val localDataSource: FavoriteLocalDataSource,
    val settingsDataStore: SettingsDataStore
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeatherData(): Flow<Result<WeatherData>> {
        return settingsDataStore.settingsFlow.flatMapLatest { settings ->
            val langCode = if (settings.language == "ARABIC") "ar" else "en"
            val units = if (settings.tempUnit == "FAHRENHEIT") "imperial" else "metric"

            if (settings.locProvider == "MANUAL" && settings.manualLat != 0.0 && settings.manualLon != 0.0) {
                flow {
                    try {
                        val current = remoteDataSource.getCurrentWeather(settings.manualLat, settings.manualLon, units, langCode)
                        val forecast = remoteDataSource.getForecast(settings.manualLat, settings.manualLon, units, langCode)
                        val mapped = mapResponseToData(current, forecast, settings)
                        emit(Result.success(mapped))
                    } catch (e: Exception) {
                        emit(Result.failure(e))
                    }
                }
            } else {
                locationProvider.fetchLocation().flatMapLatest { coords ->
                    flow {
                        try {
                            val current = remoteDataSource.getCurrentWeather(coords.first, coords.second, units, langCode)
                            val forecast = remoteDataSource.getForecast(coords.first, coords.second, units, langCode)
                            val mapped = mapResponseToData(current, forecast, settings)
                            emit(Result.success(mapped))
                        } catch (e: Exception) {
                            emit(Result.failure(e))
                        }
                    }
                }
            }
        }
    }

    fun getWeatherDataForLocation(lat: Double, lon: Double): Flow<Result<WeatherData>> {
        return settingsDataStore.settingsFlow.flatMapLatest { settings ->
            flow {
                try {
                    val langCode = if (settings.language == "ARABIC") "ar" else "en"
                    val units = if (settings.tempUnit == "FAHRENHEIT") "imperial" else "metric"
                    
                    val current = remoteDataSource.getCurrentWeather(lat, lon, units, langCode)
                    val forecast = remoteDataSource.getForecast(lat, lon, units, langCode)
                    val mapped = mapResponseToData(current, forecast, settings)
                    emit(Result.success(mapped))
                } catch (e: Exception) {
                    emit(Result.failure(e))
                }
            }
        }
    }

    val allFavorites: Flow<List<FavoriteCityEntity>> = localDataSource.allFavorites

    suspend fun insert(city: FavoriteCityEntity) = localDataSource.insertCity(city)

    suspend fun delete(city: FavoriteCityEntity) = localDataSource.deleteCity(city)
}