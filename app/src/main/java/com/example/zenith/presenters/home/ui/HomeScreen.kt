package com.example.zenith.presenters.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// Components are in the same package now.
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        }

        uiState.weatherData?.let { data ->
            val isRainy = data.description.contains("rain", ignoreCase = true) ||
                    data.description.contains("drizzle", ignoreCase = true)

            WeatherBackground(isDay = data.isDay) {
                if (isRainy) {
                    RainAnimation()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = {
                                Text(
                                    if (data.isArabic) "البحث عن مدينة..." else "Search City...",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassCard(data.isDay, 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (searchText.isNotEmpty()) {
                                        // TODO: Search logic
                                    }
                                }) {
                                    Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    item { WeatherHeader(data, data.isDay) }
                    item { MainWeatherCard(data, data.isDay) }
                    item { HourlyForecastRow(data.hourlyForecast, data.isDay, data.isArabic) }
                    item { DailyForecastList(data.dailyForecast, data.isDay, data.isArabic) }
                }
            }
        }

        uiState.errorMessage?.let { errorMsg ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $errorMsg", color = Color.Red)
            }
        }
    }
}