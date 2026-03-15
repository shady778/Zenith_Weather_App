package com.example.zenith

import android.Manifest
import android.content.IntentSender
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenith.data.datasource.local.database.LocalDataSource
import com.example.zenith.data.location.LocationProvider
import com.example.zenith.utils.LocationMonitor
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.network.RetrofitInstance
import com.example.zenith.data.db.AppDatabase
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.ui.components.ErrorScreen
import com.example.zenith.ui.theme.ZenithTheme
import com.example.zenith.presentation.home.viewmodel.WeatherViewModel
import com.example.zenith.presentation.home.viewmodel.WeatherViewModelFactory
import com.example.zenith.presentation.settings.viewmodel.SettingsViewModel
import com.example.zenith.presentation.settings.viewmodel.SettingsViewModelFactory
import com.example.zenith.presentation.splash.view.SplashScreen
import com.example.zenith.presentation.splash.viewmodel.SplashState
import com.example.zenith.presentation.splash.viewmodel.SplashViewModel
import com.example.zenith.utils.NetworkMonitor
import com.example.zenith.utils.StringHelper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = RetrofitInstance.apiService
        val remoteDataSource = WeatherRemoteDataSource(apiService)
        val weatherDao = AppDatabase.getDatabase(this).weatherDao()
        val alertDao   = AppDatabase.getDatabase(this).alertDao()
        val favDao = AppDatabase.getDatabase(this).favoriteCityDao()
        val locationProvider = LocationProvider(this)
        val localDataSource = LocalDataSource(favDao, alertDao,weatherDao)
        val settingsDataStore = SettingsDataStore(this)
        val networkMonitor = NetworkMonitor(this)
        val locationMonitor = LocationMonitor(this)
        val repository = WeatherRepository(
            remoteDataSource,
            locationProvider,
            localDataSource,
            settingsDataStore,
        )
        val factory = WeatherViewModelFactory(repository, networkMonitor, locationMonitor)

        setContent {
            val splashViewModel: SplashViewModel = viewModel()
            val weatherViewModel: WeatherViewModel = viewModel(factory = factory)
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(settingsDataStore)
            )
            val splashState by splashViewModel.state.collectAsState()
            val weatherUiState by weatherViewModel.uiState.collectAsState()

            val settingLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    splashViewModel.onLocationEnabled()
                    weatherViewModel.fetchWeather()
                } else {
                    splashViewModel.updateState(SplashState.Error("Location service is required"))
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (granted) {
                    splashViewModel.onPermissionsGranted()
                    checkLocationSettings(
                        onEnabled = {
                            splashViewModel.onLocationEnabled()
                            weatherViewModel.fetchWeather()
                        },
                        onDisabled = { intentSender ->
                            settingLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        }
                    )
                } else {
                    val isArabic = settingsViewModel.settingsState.value.language == "ARABIC"
                    splashViewModel.updateState(SplashState.Error(
                        StringHelper.getString(this@MainActivity, R.string.location_permission_error, isArabic)
                    ))
                }
            }

            LaunchedEffect(Unit) {
                if (splashState is SplashState.Idle) {
                    splashViewModel.updateState(SplashState.RequestingPermissions)
                    val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    }
                    permissionLauncher.launch(permissions)
                }
            }

            val settings by settingsViewModel.settingsState.collectAsState()
            val isLocationEnabled by locationMonitor.isLocationEnabled.collectAsState(initial = true)


            LaunchedEffect(splashState, weatherUiState) {
                if (splashState is SplashState.LoadingData) {
                    if (weatherUiState.weatherData != null) {
                        splashViewModel.updateState(SplashState.Ready)
                    } else if (weatherUiState.errorMessage != null) {
                        splashViewModel.updateState(SplashState.Error(weatherUiState.errorMessage!!))
                    }
                }
            }

            ZenithTheme {
                val onRetry = {
                    splashViewModel.updateState(SplashState.RequestingPermissions)
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }

                Crossfade(targetState = splashState, label = "screen_transition") { state ->
                    when (state) {
                        is SplashState.Ready -> {
                            MainScreen(
                                weatherViewModel = weatherViewModel,
                                settingsViewModel = settingsViewModel,
                                networkMonitor = networkMonitor
                            )
                        }
                        is SplashState.Error -> {
                            ErrorScreen(
                                message = (state as SplashState.Error).message,
                                onRetry = onRetry
                            )
                        }
                        else -> {
                            SplashScreen(viewModel = splashViewModel)
                        }
                    }
                }
            }
        }
    }
    private fun checkLocationSettings(
        onEnabled: () -> Unit,
        onDisabled: (IntentSender) -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { onEnabled() }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    onDisabled(exception.resolution.intentSender)
                } catch (sendEx: IntentSender.SendIntentException) { }
            }
        }
    }
}