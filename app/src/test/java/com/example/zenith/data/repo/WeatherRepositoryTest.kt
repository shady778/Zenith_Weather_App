package com.example.zenith.data.repo
import app.cash.turbine.test
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.datasource.local.database.LocalDataSource
import com.example.zenith.data.datasource.remote.ForecastItem
import com.example.zenith.data.datasource.remote.ForecastResponse
import com.example.zenith.data.datasource.remote.WeatherDescription
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.datasource.remote.WeatherResponse
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.local.datastore.UserSettings
import com.example.zenith.data.location.LocationProvider
import com.example.zenith.presentation.alerts.view.AlertType
import com.example.zenith.presentation.alerts.view.RepeatMode
import com.example.zenith.presentation.alerts.view.WeatherTrigger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)

class WeatherRepositoryTest {
        private val remoteDataSource = mockk<WeatherRemoteDataSource>()
        private val localDataSource = mockk<LocalDataSource>(relaxed = true)
        private val locationProvider = mockk<LocationProvider>()
        private val settingsDataStore = mockk<SettingsDataStore>()

        private lateinit var repository: WeatherRepository

        @Before
        fun setup() {
            val mockSettings = mockk<UserSettings>(relaxed = true)


            every { settingsDataStore.settingsFlow } returns flowOf(mockSettings)

            repository = WeatherRepository(remoteDataSource, locationProvider, localDataSource, settingsDataStore)
        }

        @Test
        fun getWeatherDataForLocation_success_emitsSuccess() = runTest {
            val weatherItem = mockk<WeatherDescription>(relaxed = true)
            val mockWeather = mockk<WeatherResponse>(relaxed = true)
            every { mockWeather.weather } returns listOf(weatherItem)
            every { mockWeather.main } returns mockk(relaxed = true)

            val forecastItem = mockk<ForecastItem>(relaxed = true)
            val mockForecast = mockk<ForecastResponse>(relaxed = true)
            every { mockForecast.list } returns listOf(forecastItem, forecastItem) // ليست فيها عناصر

            coEvery { remoteDataSource.getCurrentWeather(any(), any(), any(), any()) } returns mockWeather
            coEvery { remoteDataSource.getForecast(any(), any(), any(), any()) } returns mockForecast

            repository.getWeatherDataForLocation(31.2, 29.9).test {
                val result = awaitItem()

                if (result.isFailure) {
                    val error = result.exceptionOrNull()
                    fail("Failed to get weather data: ${error?.message}\n${error?.stackTraceToString()}")
                }

                assertTrue("Success", result.isSuccess)
                cancelAndIgnoreRemainingEvents()
            }
        }
        @Test
        fun getWeatherDataForLocation_error_emitsFailure() = runTest {
            coEvery { remoteDataSource.getCurrentWeather(any(), any(), any(), any()) } throws Exception("Network Error")

            val result = repository.getWeatherDataForLocation(0.0, 0.0).first()

            assertTrue(result.isFailure)
        }

        @Test
        fun insertFavorite_callsLocalDataSource() = runTest {
            val city = FavoriteCityEntity(name = "Cairo", country = "EG", lat = 0.0, lon = 0.0)

            repository.insert(city)

            coVerify { localDataSource.insertCity(city) }
        }

        @Test
        fun deleteFavorite_callsLocalDataSource() = runTest {
            val city = FavoriteCityEntity(name = "Cairo", country = "EG", lat = 0.0, lon = 0.0)

            repository.delete(city)

            coVerify { localDataSource.deleteCity(city) }
        }

        @Test
        fun getAlertById_returnsCorrectAlert() = runTest {
            val alertId = "alert_1"
            val mockAlert =  AlertEntity(
            id = "alert_1", hour = 10, minute = 30, type = AlertType.ALARM,
            trigger = WeatherTrigger.TEMPERATURE, triggerValue = 25,
            repeat = RepeatMode.ONCE, isEnabled = true, label = "Test Alert"
        )

            coEvery { localDataSource.getAlertById(alertId) } returns mockAlert

            val result = repository.getAlertById(alertId)

            assertEquals("ID Not Found", alertId, result?.id)
        }
        @Test
        fun insertAlert_callsLocalDataSource() = runTest {
            val alert = AlertEntity(
                id = "alert_1", hour = 10, minute = 30, type = AlertType.ALARM,
                trigger = WeatherTrigger.TEMPERATURE, triggerValue = 25,
                repeat = RepeatMode.ONCE, isEnabled = true, label = "Test Alert"
            )

            repository.insertAlert(alert)

            coVerify { localDataSource.insertAlert(alert) }
        }

        @Test
        fun deleteAlert_callsLocalDataSource() = runTest {
            val alert = AlertEntity(
                id = "alert_1", hour = 10, minute = 30, type = AlertType.ALARM,
                trigger = WeatherTrigger.TEMPERATURE, triggerValue = 25,
                repeat = RepeatMode.ONCE, isEnabled = true, label = "Test Alert"
            )

            repository.deleteAlert(alert)

            coVerify { localDataSource.deleteAlert(alert) }
        }
    }

