package com.example.zenith.data.datasource.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: String): AlertEntity?
}