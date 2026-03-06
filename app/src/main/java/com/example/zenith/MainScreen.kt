package com.example.zenith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import com.example.zenith.presenters.navigation.ZenithNavGraph
import com.example.zenith.presenters.navigation.ZenithBottomNav


@Composable
fun MainScreen(weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val uiState by weatherViewModel.uiState.collectAsState()
    val isDay = uiState.weatherData?.isDay ?: false

    Scaffold(
        bottomBar = {
            ZenithBottomNav(
                currentRoute = currentRoute,
                isDay = isDay,
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
            isDay = isDay,
            modifier = Modifier.padding(innerPadding)
        )
    }
}