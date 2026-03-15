package com.example.zenith.presenters.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presentation.home.viewmodel.WeatherViewModel
import com.example.zenith.utils.LocationMonitor
import com.example.zenith.utils.NetworkMonitor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WeatherViewModel

    private val repo = mockk<WeatherRepository>(relaxed = true)
    private val networkMonitor = mockk<NetworkMonitor>(relaxed = true)
    private val locationMonitor = mockk<LocationMonitor>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { networkMonitor.isConnected } returns flowOf(true)
        every { locationMonitor.isLocationEnabled } returns flowOf(true)

        viewModel = WeatherViewModel(repo, networkMonitor, locationMonitor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWeather_success_updatesUiStateWithData() = runTest(testDispatcher) {
        val mockData = mockk<WeatherData>(relaxed = true)
        coEvery { repo.getWeatherData() } returns flowOf(Result.success(mockData))

        viewModel.fetchWeather()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.weatherData, notNullValue())
        assertThat(state.isLoading, `is`(false))
        assertThat(state.errorMessage, nullValue())
    }

    @Test
    fun fetchWeather_failure_updatesUiStateWithErrorMessage() = runTest(testDispatcher) {
        val errorMessage = "Network Error"
        coEvery { repo.getWeatherData() } returns flowOf(Result.failure(Exception(errorMessage)))

        viewModel.fetchWeather()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.errorMessage, `is`(errorMessage))
        assertThat(state.isLoading, `is`(false))
    }

    @Test
    fun dismissNoInternetAlert_setsAlertFlagToFalse() {
        viewModel.dismissNoInternetAlert()
        assertThat(viewModel.uiState.value.showNoInternetAlert, `is`(false))
    }

    @Test
    fun networkDisconnected_updatesUiStateIsOnlineToFalse() = runTest(testDispatcher) {
        every { networkMonitor.isConnected } returns flowOf(false)
        val vm = WeatherViewModel(repo, networkMonitor, locationMonitor)

        advanceUntilIdle()
        assertThat(vm.uiState.value.isOnline, `is`(false))
    }
}