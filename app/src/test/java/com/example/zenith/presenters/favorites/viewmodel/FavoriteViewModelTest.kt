package com.example.zenith.presenters.favorites.viewmodel

import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.local.datastore.UserSettings
import com.example.zenith.utils.NetworkMonitor
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presentation.favorites.viewmodel.FavoriteUiState
import com.example.zenith.presentation.favorites.viewmodel.FavoriteViewModel
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
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<WeatherRepository>(relaxed = true)
    private val networkMonitor = mockk<NetworkMonitor>(relaxed = true)

    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { networkMonitor.isConnected } returns flowOf(true)

        every { repository.allFavorites } returns flowOf(emptyList())
        every { repository.settingsDataStore.settingsFlow } returns flowOf(UserSettings(
            tempUnit = "CELSIUS",
            windUnit = "MS",
            language = "ENGLISH",
            locProvider = "GPS",
            notifsEnabled = true,
            dailyAlerts = false
        ))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = FavoriteViewModel(repository, networkMonitor, testDispatcher)
    }

    @Test
    fun loadFavorites_whenEmpty_emitsEmptyState() = runTest(testDispatcher) {
        every { repository.allFavorites } returns flowOf(emptyList())
        createViewModel()

        advanceUntilIdle()

        assert(viewModel.uiState.value is FavoriteUiState.Empty)
    }

    @Test
    fun loadFavorites_whenNetworkOff_emitsSuccessWithOfflineMessage() = runTest(testDispatcher) {
        val city = FavoriteCityEntity(id = 1, name = "Cairo", lat = 30.0, lon = 31.0, country = "EG")
        every { repository.allFavorites } returns flowOf(listOf(city))
        every { networkMonitor.isConnected } returns flowOf(false)
        coEvery { repository.remoteDataSource.getCurrentWeather(any(), any(), any(), any()) } throws Exception("Offline")
        
        createViewModel()

        advanceUntilIdle()
        val state = viewModel.uiState.value
        assert(state is FavoriteUiState.Success)
        val cities = (state as FavoriteUiState.Success).cities
        assert(cities.any { it.description == "Offline" })
    }

    @Test
    fun addCity_callsRepositoryInsert() = runTest(testDispatcher) {
        createViewModel()
        val city = FavoriteCityEntity(id = 1, name = "Cairo", lat = 30.0, lon = 31.0, country = "EG")

        viewModel.addCity(city)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insert(city) }
    }

    @Test
    fun removeCity_callsRepositoryDelete() = runTest(testDispatcher) {
        createViewModel()
        val city = FavoriteCityEntity(id = 1, name = "Cairo", lat = 30.0, lon = 31.0, country = "EG")

        viewModel.removeCity(city)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.delete(city) }
    }
}