package com.example.zenith.presenters.alerts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presentation.alerts.logic.AlertScheduler
import com.example.zenith.presentation.alerts.view.AlertType
import com.example.zenith.presentation.alerts.view.RepeatMode
import com.example.zenith.presentation.alerts.view.WeatherTrigger
import com.example.zenith.presentation.alerts.viewmodel.AlertViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
class AlertViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<WeatherRepository>(relaxed = true)
    private val scheduler = mockk<AlertScheduler>(relaxed = true)
    private lateinit var viewModel: AlertViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.allAlerts } returns flowOf(emptyList())
        viewModel = AlertViewModel(repository, scheduler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addAlert_enabled_schedulesAlert() = runTest(testDispatcher) {
        val alert = AlertEntity("1", 10, 30, AlertType.ALARM, WeatherTrigger.TEMPERATURE, 25, RepeatMode.EVERY_DAY, true, "Morning")

        viewModel.addAlert(alert)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertAlert(alert) }
        coVerify(exactly = 1) { scheduler.schedule(alert) }
    }

    @Test
    fun deleteAlert_cancelsAlert() = runTest(testDispatcher) {
        val alert = AlertEntity("1", 10, 30, AlertType.ALARM, WeatherTrigger.TEMPERATURE, 25, RepeatMode.EVERY_DAY, true, "Morning")

        viewModel.deleteAlert(alert)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteAlert(alert) }
        coVerify(exactly = 1) { scheduler.cancel(alert) }
    }

    @Test
    fun toggleAlert_fromEnabledToDisabled_cancelsAlert() = runTest(testDispatcher) {
        val alert = AlertEntity("1", 10, 30, AlertType.ALARM, WeatherTrigger.TEMPERATURE, 25, RepeatMode.EVERY_DAY, true, "Morning")

        viewModel.toggleAlert(alert)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertAlert(match { !it.isEnabled }) }
        coVerify(exactly = 1) { scheduler.cancel(any()) }
    }
}