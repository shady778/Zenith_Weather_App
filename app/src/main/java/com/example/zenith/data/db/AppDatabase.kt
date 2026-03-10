package com.example.zenith.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.zenith.data.datasource.local.database.AlertDao
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.FavoriteCityDao
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity

@Database(entities = [FavoriteCityEntity::class, AlertEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteCityDao(): FavoriteCityDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zenith_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}