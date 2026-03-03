package com.example.zenith.data.datasource

import com.example.zenith.data.datasource.remote.WeatherApiService
import com.example.zenith.data.datasource.remote.WeatherResponse
import com.example.zenith.data.datasource.remote.ForecastResponse

class WeatherRemoteDataSourceImpl(
    private val apiService: WeatherApiService
) {

    private val apiKey = "YOUR_API_KEY"

    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        return apiService.getCurrentWeather(lat, lon, apiKey)
    }

   suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return apiService.getForecast(lat, lon, apiKey)
    }
}