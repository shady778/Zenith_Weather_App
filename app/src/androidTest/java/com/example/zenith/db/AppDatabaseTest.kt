package com.example.zenith.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zenith.data.datasource.local.database.AlertDao
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.FavoriteCityDao
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.datasource.local.database.WeatherDao
import com.example.zenith.data.datasource.local.database.WeatherEntity
import com.example.zenith.data.db.AppDatabase
import com.example.zenith.data.model.DailyForecast
import com.example.zenith.data.model.HourlyForecast
import com.example.zenith.data.model.WeatherData
import com.example.zenith.presentation.alerts.view.AlertType
import com.example.zenith.presentation.alerts.view.RepeatMode
import com.example.zenith.presentation.alerts.view.WeatherTrigger
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var favoriteDao: FavoriteCityDao
    private lateinit var alertDao: AlertDao
    private lateinit var weatherDao: WeatherDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        favoriteDao = db.favoriteCityDao()
        alertDao = db.alertDao()
        weatherDao = db.weatherDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadFavoriteCity() = runTest {
        val city = FavoriteCityEntity(name = "Alexandria", country = "Egypt", lat = 31.2, lon = 29.9)
        favoriteDao.insertCity(city)

        val allFavorites = favoriteDao.getAllFavorites().first()
        assertEquals(1, allFavorites.size)
        assertEquals("Alexandria", allFavorites[0].name)
    }

    @Test
    fun deleteFavoriteCity() = runTest {
        val city = FavoriteCityEntity(name = "Cairo", country = "Egypt", lat = 30.0, lon = 31.0)
        favoriteDao.insertCity(city)
        val savedCity = favoriteDao.getAllFavorites().first()[0]
        favoriteDao.deleteCity(savedCity)

        val allFavorites = favoriteDao.getAllFavorites().first()
        assertTrue(allFavorites.isEmpty())
    }

    @Test
    fun insertAndGetAlertById() = runTest {
        val alert = AlertEntity(
            id = "alert_1",
            hour = 8,
            minute = 30,
            type = AlertType.ALARM,
            trigger = WeatherTrigger.ANY,
            triggerValue = null,
            repeat = RepeatMode.EVERY_DAY,
            isEnabled = true,
            label = "Morning Alert"
        )
        alertDao.insertAlert(alert)

        val fetchedAlert = alertDao.getAlertById("alert_1")
        assertNotNull(fetchedAlert)
        assertEquals(8, fetchedAlert?.hour)
    }

    @Test
    fun insertAndClearWeatherCache() = runTest {
        val mockHourly = listOf(
            HourlyForecast(time = "12:00 PM", temp = "25°C", icon = "01d")
        )
        val mockDaily = listOf(
            DailyForecast(
                day = "Monday",
                highTemp = "30°C",
                lowTemp = "20°C",
                description = "Sunny",
                icon = "01d"
            )
        )

        val mockWeatherData = WeatherData(
            localTime = "2024-03-21 10:00",
            city = "Alexandria",
            country = "Egypt",
            temperature = "25°C",
            description = "Clear Sky",
            icon = "01d",
            humidity = "60%",
            windSpeed = "10 km/h",
            clouds = "10%",
            pressure = "1012 hPa",
            hourlyForecast = mockHourly,
            dailyForecast = mockDaily,
            isDay = true,
            lat = 31.2,
            lon = 29.9,
            isArabic = false
        )

        val cache = WeatherEntity(id = 0, data = mockWeatherData)
        weatherDao.insertWeatherCache(cache)

        val savedCache = weatherDao.getWeatherCache().first()
        assertNotNull(savedCache)
        assertEquals("Alexandria", savedCache?.data?.city)

        weatherDao.clearWeatherCache()
        val result = weatherDao.getWeatherCache().first()
        assertNull(result)
    }
}