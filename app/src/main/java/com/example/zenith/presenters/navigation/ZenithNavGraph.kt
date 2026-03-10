package com.example.zenith.presenters.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zenith.presenters.home.ui.HomeScreen
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import com.example.zenith.presenters.favorites.view.FavoritesScreen
import com.example.zenith.presenters.favorites.viewmodel.FavoriteViewModel
import com.example.zenith.presenters.home.viewmodel.WeatherUiState
import com.example.zenith.presenters.settings.view.SettingsScreen
import com.example.zenith.presenters.settings.viewmodel.SettingsViewModel
import com.example.zenith.presenters.alerts.view.AlertsScreen
import com.example.zenith.presenters.alerts.viewmodel.AlertViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ZenithNavGraph(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel,
    favoriteViewModel: FavoriteViewModel,
    settingsViewModel: SettingsViewModel,
    alertViewModel: AlertViewModel,
    isDay: Boolean,
    modifier: Modifier = Modifier

) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = weatherViewModel)
        }
        composable(Screen.Favorites.route) {
            val weatherUiState = weatherViewModel.uiState.collectAsState().value
            val settings = settingsViewModel.settingsState.collectAsState().value

            FavoritesScreen(
                viewModel = favoriteViewModel, 
                isDay = isDay,
                isArabic = settings.language == "ARABIC",
                currentWeather = weatherUiState.weatherData,
                onCitySelected = { lat, lon ->
                    favoriteViewModel.viewDetail(lat, lon)
                }
            )
        }

        composable(Screen.Alerts.route) {
            val settings = settingsViewModel.settingsState.collectAsState().value
            AlertsScreen(
                isDay = isDay, 
                isArabic = settings.language == "ARABIC",
                viewModel = alertViewModel
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                isDay = isDay,
                viewModel = settingsViewModel,
                onLocationChanged = { weatherViewModel.fetchWeather() }
            )
        }
    }
}


