package com.example.zenith.data.repo

import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.mapResponseToData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDataSource
) {
    fun getWeatherData(lat: Double, lon: Double): Flow<Result<WeatherData>> = flow {
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