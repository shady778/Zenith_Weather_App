package com.example.zenith.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import com.example.zenith.data.datasource.local.database.FavoriteCityDao
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity

@Database(entities = [FavoriteCityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteCityDao(): FavoriteCityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zenith_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}