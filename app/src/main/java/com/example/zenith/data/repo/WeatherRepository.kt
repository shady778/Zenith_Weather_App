package com.example.zenith.data.repo

import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.datasource.local.database.FavoriteLocalDataSource
import com.example.zenith.data.datasource.location.LocationProvider
import com.example.zenith.data.datasource.remote.ForecastResponse
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.datasource.remote.WeatherResponse
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.local.datastore.UserSettings
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.mapResponseToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepository(
    val remoteDataSource: WeatherRemoteDataSource,
    private val locationProvider: LocationProvider,
    private val localDataSource: FavoriteLocalDataSource,
    val settingsDataStore: SettingsDataStore
) {

    suspend fun fetchCurrent(lat: Double, lon: Double, units: String, lang: String): WeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, units, lang)
    }

    suspend fun fetchForecast(lat: Double, lon: Double, units: String, lang: String): ForecastResponse {
        return remoteDataSource.getForecast(lat, lon, units, lang)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeatherData(): Flow<Result<WeatherData>> {
        return settingsDataStore.settingsFlow.flatMapLatest { settings ->
            val config = prepareWeatherConfig(settings)

            getLocationFlow(settings).flatMapLatest { (lat, lon) ->
                executeWeatherRequest(lat, lon, config, settings)
            }
        }
    }

    private fun prepareWeatherConfig(settings: UserSettings): WeatherConfig {
        return WeatherConfig(
            lang = if (settings.language == "ARABIC") "ar" else "en",
            units = if (settings.tempUnit == "FAHRENHEIT") "imperial" else "metric"
        )
    }

    private fun getLocationFlow(settings: UserSettings): Flow<Pair<Double, Double>> {
        return if (isManualLocation(settings)) {
            flowOf(Pair(settings.manualLat, settings.manualLon))
        } else {
            locationProvider.fetchLocation()
        }
    }

    private fun isManualLocation(settings: UserSettings): Boolean {
        return settings.locProvider == "MANUAL"
    }


    private fun executeWeatherRequest(
        lat: Double,
        lon: Double,
        config: WeatherConfig,
        settings: UserSettings
    ): Flow<Result<WeatherData>> = flow {
        try {
            val current = fetchCurrent(lat, lon, config.units, config.lang)
            val forecast = fetchForecast(lat, lon, config.units, config.lang)

            val mapped = mapResponseToData(current, forecast, settings)
            emit(Result.success(mapped))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    private data class WeatherConfig(val lang: String, val units: String)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeatherDataForLocation(lat: Double, lon: Double): Flow<Result<WeatherData>> {
        return settingsDataStore.settingsFlow.flatMapLatest { settings ->
            flow {
                try {
                    val config = prepareWeatherConfig(settings)

                    val current = fetchCurrent(lat, lon, config.units, config.lang)
                    val forecast = fetchForecast(lat, lon, config.units, config.lang)

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