package com.example.zenith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.network.RetrofitInstance
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presenters.home.view.WeatherHomeScreen
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import com.example.zenith.presenters.home.viewmodel.WeatherViewModelFactory





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = RetrofitInstance.apiService
        val remoteDataSource = WeatherRemoteDataSource(apiService)
        val repository = WeatherRepository(remoteDataSource)
        val factory = WeatherViewModelFactory(repository)

        setContent {
            val viewModel: WeatherViewModel = viewModel(factory = factory)

            LaunchedEffect(Unit) {
                viewModel.fetchWeather(30.0444, 31.2357)
            }

            WeatherHomeScreen(viewModel = viewModel)
        }
    }
}