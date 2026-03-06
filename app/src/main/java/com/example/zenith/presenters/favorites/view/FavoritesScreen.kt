package com.example.zenith.presenters.favorites.view

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.presenters.favorites.viewmodel.*
import com.example.zenith.ui.components.ZenithTopBar
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.presenters.home.ui.WeatherBackground
import androidx.compose.ui.graphics.Color

@Composable
fun FavoritesScreen(isDay: Boolean) {
    var cities by remember { mutableStateOf(sampleCities) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCelsius by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val selectionInteractionSource = remember { MutableInteractionSource() }


    WeatherBackground(isDay = isDay) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ZenithColors.Cyan,
                                    ZenithColors.Cyan.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { showAddDialog = true }
                        )
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = "Add",
                        tint = ZenithColors.Background,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                ZenithTopBar(
                    title = "Favorites",
                    subtitle = "${cities.size} cities saved",
                    actions = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ZenithColors.SurfaceGlass)
                                .border(1.dp, ZenithColors.BorderGlass, RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = selectionInteractionSource,
                                    indication = null,
                                    onClick = { showCelsius = !showCelsius }
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (showCelsius) "°C" else "°F",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenithColors.Cyan
                            )
                        }
                    }
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(cities) { index, city ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300, delayMillis = index * 60)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = tween(300, delayMillis = index * 60)
                                    )
                        ) {
                            CityCard(
                                city = city,
                                showCelsius = showCelsius,
                                onDelete = { cities = cities.filterNot { it.id == city.id } }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            MapPicker(
                onDismiss = { showAddDialog = false },
                onLocationSelected = { name, country ->
                    cities = cities + FavoriteCity(
                        id = (cities.maxOfOrNull { it.id } ?: 0) + 1,
                        name = name,
                        country = country,
                        tempC = (5..35).random(),
                        condition = WeatherCondition.entries.random(),
                        humidity = (20..38).random(),
                        wind = (0..10).random(),
                        localTime = "12:00 PM"
                    )

                }
            )
        }
    }
}