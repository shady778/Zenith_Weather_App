package com.example.zenith.data.repo

import com.example.zenith.data.datasource.location.LocationProvider
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.mapResponseToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val locationProvider: LocationProvider
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

}