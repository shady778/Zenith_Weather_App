package com.example.zenith.data.datasource.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE id = 0")
    fun getWeatherCache(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(cache: WeatherEntity)

    @Query("DELETE FROM weather_cache")
    suspend fun clearWeatherCache()
}
