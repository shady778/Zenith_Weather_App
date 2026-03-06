package com.example.zenith.presenters.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zenith.presenters.home.ui.HomeScreen
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel

@Composable
fun ZenithNavGraph(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel,
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
            com.example.zenith.presenters.favorites.view.FavoritesScreen(isDay = isDay)
        }
        composable(Screen.Alerts.route) {
        }
        composable(Screen.Settings.route) {
        }
    }
}