package com.example.zenith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import com.example.zenith.presenters.navigation.ZenithNavGraph
import com.example.zenith.presenters.navigation.ZenithBottomNav
import com.example.zenith.presenters.favorites.viewmodel.FavoriteViewModel
import com.example.zenith.presenters.favorites.viewmodel.FavoriteViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.presenters.settings.viewmodel.SettingsViewModel
import com.example.zenith.presenters.settings.viewmodel.SettingsViewModelFactory
import com.example.zenith.presenters.alerts.viewmodel.AlertViewModel
import com.example.zenith.presenters.alerts.viewmodel.AlertViewModelFactory
import com.example.zenith.presenters.alerts.logic.AlertScheduler


@Composable
fun MainScreen(weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(weatherViewModel.repository) 
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            SettingsDataStore(LocalContext.current)
        )
    )

    val context = LocalContext.current
    val alertViewModel: AlertViewModel = viewModel(
        factory = AlertViewModelFactory(
            weatherViewModel.repository,
            AlertScheduler(context)
        )
    )

    val uiState by weatherViewModel.uiState.collectAsState()
    val isDay = uiState.weatherData?.isDay ?: false

    val settings by settingsViewModel.settingsState.collectAsState()
    val layoutDirection = if (settings.language == "ARABIC") 
        androidx.compose.ui.unit.LayoutDirection.Rtl 
    else 
        androidx.compose.ui.unit.LayoutDirection.Ltr

    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection
    ) {
        Scaffold(
            bottomBar = {
                ZenithBottomNav(
                    currentRoute = currentRoute,
                    isDay = isDay,
                    isArabic = settings.language == "ARABIC",
                    onSelect = { selectedScreen ->
                        navController.navigate(selectedScreen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            ZenithNavGraph(
                navController = navController,
                weatherViewModel = weatherViewModel,
                favoriteViewModel = favoriteViewModel,
                settingsViewModel = settingsViewModel,
                alertViewModel = alertViewModel,
                isDay = isDay,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
