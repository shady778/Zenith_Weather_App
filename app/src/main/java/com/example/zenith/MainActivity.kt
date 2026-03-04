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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenith.data.datasource.location.LocationProvider
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.network.RetrofitInstance
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presenters.home.view.WeatherHomeScreen
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import com.example.zenith.presenters.home.viewmodel.WeatherViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = RetrofitInstance.apiService
        val remoteDataSource = WeatherRemoteDataSource(apiService)
        val locationProvider = LocationProvider(this)
        val repository = WeatherRepository(remoteDataSource, locationProvider)
        val factory = WeatherViewModelFactory(repository)

        setContent {
            val viewModel: WeatherViewModel = viewModel(factory = factory)

            val settingLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.fetchWeather()
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (granted) {
                    checkLocationSettings(
                        onEnabled = { viewModel.fetchWeather() },
                        onDisabled = { intentSender ->
                            settingLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        }
                    )
                }
            }

            LaunchedEffect(Unit) {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }

            WeatherHomeScreen(viewModel = viewModel)
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
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }
}