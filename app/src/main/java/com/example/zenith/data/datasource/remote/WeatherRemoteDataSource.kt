package com.example.zenith.data.datasource.remote

class WeatherRemoteDataSource(
    private val apiService: WeatherApiService
) {
 
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, langCode: String): WeatherResponse {
        return apiService.getCurrentWeather(lat, lon, units = units, lang = langCode)
    }
 
    suspend fun getForecast(lat: Double, lon: Double, units: String, langCode: String): ForecastResponse {
        return apiService.getForecast(lat, lon, units = units, lang = langCode)
    }
}