package com.example.zenith.data.model

data class WeatherData(
    val localTime: String,
    val city: String,
    val country: String,
    val temperature: String,
    val description: String,
    val icon: String,
    val humidity: String,
    val windSpeed: String,
    val clouds: String,
    val pressure: String,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
    val isDay: Boolean,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

data class HourlyForecast(
    val time: String,
    val temp: String,
    val icon: String
)

data class DailyForecast(
    val day: String,
    val highTemp: String,
    val lowTemp: String,
    val description: String,
    val icon: String
)