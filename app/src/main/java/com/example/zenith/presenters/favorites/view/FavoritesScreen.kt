package com.example.zenith.presenters.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.presenters.favorites.viewmodel.FavoriteUiState
import com.example.zenith.presenters.favorites.viewmodel.FavoriteCity
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.ui.components.ZenithDeleteDialog
import com.example.zenith.ui.components.WeatherDetailsDialog
import com.example.zenith.data.model.WeatherData
import com.example.zenith.presenters.favorites.viewmodel.FavoriteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel,
    isDay: Boolean,
    isArabic: Boolean = false,
    currentWeather: WeatherData? = null,
    onCitySelected: (Double, Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    var showMapPicker by remember { mutableStateOf(false) }
    var cityToDelete by remember { mutableStateOf<FavoriteCity?>(null) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDay) listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                    else listOf(Color(0xFF050B18), Color(0xFF0A1628))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (isArabic) "المدن المفضلة" else "Favorite Cities",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showMapPicker = true },
                    containerColor = ZenithColors.Cyan,
                    contentColor = ZenithColors.Background,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = if (isArabic) "إضافة مدينة" else "Add City", modifier = Modifier.size(32.dp))
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val state = uiState) {
                    is FavoriteUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = ZenithColors.Cyan
                        )
                    }
                    is FavoriteUiState.Empty -> {
                        EmptyState(isArabic = isArabic, modifier = Modifier.align(Alignment.Center))
                    }
                    is FavoriteUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.cities) { city ->
                                CityCard(
                                    city = city,
                                    onClick = { 
                                        onCitySelected(city.entity.lat, city.entity.lon)
                                    },
                                    onDelete = { cityToDelete = city }
                                )
                            }

                        }
                    }
                    is FavoriteUiState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        if (showMapPicker) {
            MapPicker(
                initialLat = if (currentWeather?.lat != 0.0) currentWeather?.lat ?: 30.0444 else 30.0444,
                initialLon = if (currentWeather?.lon != 0.0) currentWeather?.lon ?: 31.2357 else 31.2357,
                isArabic = isArabic,
                onDismiss = { showMapPicker = false },
                onLocationSelected = { name, country, lat, lon ->
                    viewModel.addCity(
                        FavoriteCityEntity(
                            name = name,
                            country = country,
                            lat = lat,
                            lon = lon
                        )
                    )
                }
            )
        }

        cityToDelete?.let { city ->
            ZenithDeleteDialog(
                cityName = city.name,
                isArabic = isArabic,
                onDismiss = { cityToDelete = null },
                onConfirm = { 
                    viewModel.removeCity(city.entity)
                }
            )
        }

        detailState.weatherData?.let { data ->
            WeatherDetailsDialog(
                data = data,
                onDismiss = { viewModel.clearDetail() }
            )
        }

        if (detailState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ZenithColors.Cyan)
            }
        }
    }
}


@Composable
fun EmptyState(isArabic: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = ZenithColors.SurfaceGlass
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = ZenithColors.TextSecondary
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            if (isArabic) "لا توجد مدن مفضلة" else "No Favorites Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (isArabic) "اضغط على زر + لإضافة مدنك المفضلة وتتبع حالة الطقس فيها." else "Tap the + button to add your favorite cities and track their weather.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = ZenithColors.TextSecondary,
            lineHeight = 22.sp
        )
    }
}