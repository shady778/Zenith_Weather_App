package com.example.zenith.presenters.home.view

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
import com.example.zenith.presenters.home.view.ui.components.DailyForecastList
import com.example.zenith.presenters.home.view.ui.components.HourlyForecastRow
import com.example.zenith.presenters.home.view.ui.components.MainWeatherCard
import com.example.zenith.presenters.home.view.ui.components.RainAnimation
import com.example.zenith.presenters.home.view.ui.components.WeatherBackground
import com.example.zenith.presenters.home.view.ui.components.WeatherHeader
import com.example.zenith.presenters.home.view.ui.components.glassCard
import com.example.zenith.presenters.home.viewmodel.WeatherViewModel
import androidx.compose.runtime.Composable




@Composable
fun WeatherHomeScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Cyan)
        }
    }

    uiState.data?.let { data ->
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
                            Text("Search City...", color = Color.White.copy(alpha = 0.5f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard(data.isDay, 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (searchText.isNotEmpty()) {
                                    // logic search by city
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
                item { HourlyForecastRow(data.hourlyForecast, data.isDay) }
                item { DailyForecastList(data.dailyForecast, data.isDay) }
            }
        }
    }

    uiState.error?.let { errorMsg ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $errorMsg", color = Color.Red)
        }
    }
}