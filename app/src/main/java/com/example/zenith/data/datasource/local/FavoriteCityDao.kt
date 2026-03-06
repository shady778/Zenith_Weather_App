package com.example.zenith.data.datasource.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities")
    fun getAllFavorites(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: FavoriteCityEntity)

    @Delete
    suspend fun deleteCity(city: FavoriteCityEntity)
}