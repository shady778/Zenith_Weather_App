package com.example.zenith.presenters.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.local.datastore.UserSettings
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.repo.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<WeatherRepository>(relaxed = true)
    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { repository.allFavorites } returns flowOf(emptyList())

        every { repository.settingsDataStore.settingsFlow } returns flowOf(UserSettings(
            tempUnit = "CELSIUS",
            windUnit = "MS",
            language = "ENGLISH",
            locProvider = "GPS",
            notifsEnabled = true,
            dailyAlerts = false,
            manualLat = 0.0,
            manualLon = 0.0
        ))

        viewModel = FavoriteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavorites_whenEmpty_emitsEmptyState() = runTest(testDispatcher) {
        every { repository.allFavorites } returns flowOf(emptyList())
        viewModel = FavoriteViewModel(repository)

        advanceUntilIdle()

        assert(viewModel.uiState.value is FavoriteUiState.Empty)
    }

    @Test
    fun viewDetail_onSuccess_updatesDetailState() = runTest(testDispatcher) {
        val mockData = mockk<WeatherData>(relaxed = true)
        coEvery { repository.getWeatherDataForLocation(any(), any()) } returns flowOf(Result.success(mockData))

        viewModel.viewDetail(0.0, 0.0)
        advanceUntilIdle()

        val state = viewModel.detailState.value
        assert(!state.isLoading)
        assert(state.weatherData == mockData)
    }
    @Test
    fun addCity_callsRepositoryInsert() = runTest(testDispatcher) {
        val city = FavoriteCityEntity(id = 1, name = "Cairo", lat = 30.0, lon = 31.0, country = "EG")

        viewModel.addCity(city)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insert(city) }
    }

    @Test
    fun removeCity_callsRepositoryDelete() = runTest(testDispatcher) {
        val city =
            FavoriteCityEntity(id = 1, name = "Cairo", lat = 30.0, lon = 31.0, country = "EG")

        viewModel.removeCity(city)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.delete(city) }
    }
}