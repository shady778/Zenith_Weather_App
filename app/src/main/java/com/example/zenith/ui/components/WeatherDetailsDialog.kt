package com.example.zenith.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.zenith.data.model.WeatherData
import com.example.zenith.presenters.home.ui.*
import com.example.zenith.ui.theme.ZenithColors

@Composable
fun WeatherDetailsDialog(
    data: WeatherData,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (data.isDay) listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                        else listOf(Color(0xFF050B18), Color(0xFF0A1628))
                    )
                )
        ) {
            val isRainy = data.description.contains("rain", ignoreCase = true) ||
                    data.description.contains("drizzle", ignoreCase = true)

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                item { WeatherHeader(data, data.isDay) }
                item { MainWeatherCard(data, data.isDay) }
                item { HourlyForecastRow(data.hourlyForecast, data.isDay) }
                item { DailyForecastList(data.dailyForecast, data.isDay) }
            }
        }
    }
}
