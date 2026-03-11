package com.example.zenith.data.datasource.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zenith.data.model.WeatherData

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val data: WeatherData
)
