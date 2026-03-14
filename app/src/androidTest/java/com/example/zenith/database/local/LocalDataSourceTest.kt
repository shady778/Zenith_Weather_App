package com.example.zenith.database.local
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.datasource.local.database.LocalDataSource
import com.example.zenith.data.datasource.local.database.WeatherEntity
import com.example.zenith.data.db.AppDatabase
import com.example.zenith.data.model.WeatherData
import com.example.zenith.presenters.alerts.view.AlertType
import com.example.zenith.presenters.alerts.view.RepeatMode
import com.example.zenith.presenters.alerts.view.WeatherTrigger
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var db: AppDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = LocalDataSource(db.favoriteCityDao(), db.alertDao(), db.weatherDao())
    }

    @After
    fun cleanUp() {
        db.close()
    }

    @Test
    fun insertCity_savesToDatabase() = runTest {
        val city =
            FavoriteCityEntity(name = "Alexandria", country = "Egypt", lat = 31.2, lon = 29.9)
        localDataSource.insertCity(city)

        val list = localDataSource.allFavorites.first()
        Assert.assertEquals(1, list.size)
        Assert.assertEquals("Alexandria", list[0].name)
    }

    @Test
    fun deleteCity_removesFromDatabase() = runTest {
        val city = FavoriteCityEntity(name = "Cairo", country = "Egypt", lat = 30.0, lon = 29.9)
        localDataSource.insertCity(city)

        val savedCities = localDataSource.allFavorites.first()
        val cityToDelete = savedCities[0]

        localDataSource.deleteCity(cityToDelete)

        val listAfterDelete = localDataSource.allFavorites.first()
        Assert.assertTrue(listAfterDelete.isEmpty())
    }

    @Test
    fun insertAlert_retrievesById() = runTest {
        val alert = AlertEntity(
            id = "alert_1", hour = 10, minute = 30, type = AlertType.ALARM,
            trigger = WeatherTrigger.TEMPERATURE, triggerValue = 25,
            repeat = RepeatMode.ONCE, isEnabled = true, label = "Test Alert"
        )

        localDataSource.insertAlert(alert)
        val fetched = localDataSource.getAlertById("alert_1")

        Assert.assertNotNull(fetched)
        Assert.assertEquals("Test Alert", fetched?.label)
    }

    @Test
    fun deleteAlert_removesFromDatabase() = runTest {
        val alert = AlertEntity(
            id = "alert_1", hour = 10, minute = 30, type = AlertType.ALARM,
            trigger = WeatherTrigger.TEMPERATURE, triggerValue = 25,
            repeat = RepeatMode.ONCE, isEnabled = true, label = "Test Alert"
        )

        localDataSource.insertAlert(alert)
        localDataSource.deleteAlert(alert)
        val fetched = localDataSource.getAlertById("alert_1")
        Assert.assertNull(fetched)
    }

    @Test
    fun insertWeatherCache_retrievesSameData() = runTest {
        val mockData = WeatherData(
            localTime = "12:00", city = "Cairo", country = "Egypt", temperature = "25",
            description = "Sunny", icon = "01d", humidity = "50", windSpeed = "5",
            clouds = "0", pressure = "1010", hourlyForecast = emptyList(),
            dailyForecast = emptyList(), isDay = true
        )
        val cache = WeatherEntity(id = 0, data = mockData)

        localDataSource.insertWeatherCache(cache)
        val saved = localDataSource.weatherCache.first()

        Assert.assertNotNull(saved)
        Assert.assertEquals("Cairo", saved?.data?.city)
    }

    @Test
    fun clearWeatherCache_setsValueToNull() = runTest {
        localDataSource.clearWeatherCache()
        val result = localDataSource.weatherCache.first()
        Assert.assertNull(result)
    }
}