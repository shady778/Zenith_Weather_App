package com.example.zenith.presenters.settings.viewmodel

import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.local.datastore.UserSettings
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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

    @OptIn(ExperimentalCoroutinesApi::class)
    class SettingsViewModelTest {

        private val testDispatcher = StandardTestDispatcher()
        private val settingsDataStore = mockk<SettingsDataStore>(relaxed = true)
        private lateinit var viewModel: SettingsViewModel

        @Before
        fun setup() {
            Dispatchers.setMain(testDispatcher)
            val mockSettings = UserSettings("CELSIUS", "M/S", "ENGLISH", "GPS", true, true, 0.0, 0.0)
            every { settingsDataStore.settingsFlow } returns flowOf(mockSettings)
            viewModel = SettingsViewModel(settingsDataStore)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun settingsState_emitsCorrectData() = runTest(testDispatcher) {
            advanceUntilIdle()

            val currentState = viewModel.settingsState.value
            assertThat(currentState.tempUnit, `is`("CELSIUS"))
            assertThat(currentState.language, `is`("ENGLISH"))
        }

        @Test
        fun updateSettings_callsDataStore() = runTest(testDispatcher) {
            val newSettings = UserSettings("FAHRENHEIT", "MS", "ENGLISH", "GPS", true, false, 10.0, 20.0)

            viewModel.updateSettings(newSettings)
            advanceUntilIdle()

            coVerify(exactly = 1) { settingsDataStore.updateSettings(newSettings) }
        }
    }
