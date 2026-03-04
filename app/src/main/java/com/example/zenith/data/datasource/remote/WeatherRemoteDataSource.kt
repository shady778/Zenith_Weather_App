package com.example.zenith.data.datasource.remote
 
import com.example.zenith.BuildConfig
import com.example.zenith.data.datasource.remote.WeatherApiService
import com.example.zenith.data.datasource.remote.WeatherResponse
import com.example.zenith.data.datasource.remote.ForecastResponse
 
class WeatherRemoteDataSource(
    private val apiService: WeatherApiService
) {
 
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        return apiService.getCurrentWeather(lat, lon)
    }
 
   suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return apiService.getForecast(lat, lon)
    }
}