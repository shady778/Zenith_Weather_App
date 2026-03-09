package com.example.zenith.data.datasource.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)