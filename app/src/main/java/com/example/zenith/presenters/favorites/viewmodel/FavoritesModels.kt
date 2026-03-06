package com.example.zenith.presenters.favorites.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FavoriteCity(
    val id: Int,
    val name: String,
    val country: String,
    val tempC: Int,
    val condition: WeatherCondition,
    val humidity: Int,
    val wind: Int,
    val localTime: String
)

enum class WeatherCondition(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
    val gradientStart: Color
) {
    Sunny("Sunny", Icons.Rounded.WbSunny, Color(0xFFFFD600), Color(0xFFFFE082)),
    PartlyCloudy("Partly Cloudy", Icons.Rounded.CloudQueue, Color(0xFF4FC3F7), Color(0xFFB3E5FC)),
    Cloudy("Cloudy", Icons.Rounded.Cloud, Color(0xFF90A4AE), Color(0xFFCFD8DC)),
    Rainy("Rainy", Icons.Rounded.Thunderstorm, Color(0xFF4DB6AC), Color(0xFFB2DFDB)),
    Snowy("Snowy", Icons.Rounded.AcUnit, Color(0xFF90CAF9), Color(0xFFE3F2FD))
}

val sampleCities = listOf(
    FavoriteCity(1, "Cairo", "EG", 28, WeatherCondition.Sunny, 45, 12, "02:30 PM"),
    FavoriteCity(2, "London", "UK", 12, WeatherCondition.Rainy, 78, 22, "12:30 PM"),
    FavoriteCity(3, "Tokyo", "JP", 21, WeatherCondition.PartlyCloudy, 60, 15, "09:30 PM")
)
