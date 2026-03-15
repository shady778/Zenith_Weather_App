package com.example.zenith.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zenith.presentation.home.viewmodel.WeatherViewModel
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.ui.components.NoInternetDialog
import com.example.zenith.ui.components.OfflineBanner
import com.example.zenith.ui.components.LocationDisabledBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ZenithColors.Cyan)
            }
        }

        uiState.weatherData?.let { data ->
            val isRainy = data.description.contains("rain", ignoreCase = true) ||
                    data.description.contains("drizzle", ignoreCase = true)

            WeatherBackground(isDay = data.isDay) {
                if (isRainy) {
                    RainAnimation()
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    OfflineBanner(isOnline = uiState.isOnline, isArabic = data.isArabic)
                    
                    if (uiState.isGpsMode && !uiState.isLocationEnabled) {
                        LocationDisabledBanner(isArabic = data.isArabic)
                    }

                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.fetchWeather() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { WeatherHeader(data, data.isDay) }
                            item { MainWeatherCard(data, data.isDay) }
                            item { HourlyForecastRow(data.hourlyForecast, data.isDay, data.isArabic) }
                            item { DailyForecastList(data.dailyForecast, data.isDay, data.isArabic) }
                        }
                    }
                }
            }

            if (uiState.showNoInternetAlert) {
                NoInternetDialog(
                    isArabic = data.isArabic,
                    onDismiss = { viewModel.dismissNoInternetAlert() }
                )
            }
        }

        uiState.errorMessage?.let { errorMsg ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $errorMsg", color = Color.Red)
            }
        }
    }
}