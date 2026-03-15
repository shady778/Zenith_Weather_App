package com.example.zenith.presentation.favorites.view

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.zenith.ui.theme.ZenithColors
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.Toast

@Composable
fun MapPicker(
    initialLat: Double = 0.0,
    initialLon: Double = 0.0,
    isArabic: Boolean = false,
    onDismiss: () -> Unit,
    onLocationSelected: (String, String, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.controller.setZoom(12.0)

        val startPoint = if (initialLat != 0.0 && initialLon != 0.0) {
            org.osmdroid.util.GeoPoint(initialLat, initialLon)
        } else {
            org.osmdroid.util.GeoPoint(30.0444, 31.2357)
        }
        mapView.controller.setCenter(startPoint)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f))) {
        Card(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = ZenithColors.Background)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                var searchQuery by remember { mutableStateOf("") }
                val scope = rememberCoroutineScope()

                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 20.dp)
                        .align(Alignment.TopCenter),
                    color = Color.Black.copy(0.6f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                if (isArabic) "ابحث عن مدينة..." else "Search for a city...",
                                color = Color.White.copy(0.5f),
                                fontSize = 14.sp
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = ZenithColors.Cyan,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Rounded.Search, null, tint = ZenithColors.Cyan) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, if (isArabic) Locale("ar") else Locale.getDefault())
                                            val results = geocoder.getFromLocationName(searchQuery, 1)
                                            if (!results.isNullOrEmpty()) {
                                                val found = results[0]
                                                withContext(Dispatchers.Main) {
                                                    mapView.controller.animateTo(org.osmdroid.util.GeoPoint(found.latitude, found.longitude))
                                                    mapView.controller.setZoom(14.0)
                                                }
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, if(isArabic) "المكان غير موجود" else "Location not found", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Search error", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }) {
                                    Icon(Icons.Rounded.CheckCircle, null, tint = ZenithColors.Cyan)
                                }
                            }
                        },
                        singleLine = true
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(0.4f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { mapView.controller.zoomIn() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .border(2.dp, Color.White.copy(0.1f), RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = Color.Black, modifier = Modifier.size(24.dp))
                    }
                    IconButton(
                        onClick = { mapView.controller.zoomOut() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .border(2.dp, Color.White.copy(0.1f), RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Rounded.Remove, null, tint = Color.Black, modifier = Modifier.size(24.dp))
                    }
                }

                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = ZenithColors.Cyan,
                    modifier = Modifier.size(48.dp).align(Alignment.Center)
                )

                Button(
                    onClick = {
                        val center = mapView.mapCenter
                        val geocoder = Geocoder(context, if (isArabic) Locale("ar") else Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
                            val name = addresses?.firstOrNull()?.locality ?: "Unknown Location"
                            val country = addresses?.firstOrNull()?.countryName ?: "Unknown"
                            onLocationSelected(name, country, center.latitude, center.longitude)
                        } catch (e: Exception) {
                            onLocationSelected("Unknown", "Unknown", center.latitude, center.longitude)
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .height(56.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = ZenithColors.Cyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        if (isArabic) "تأكيد الموقع" else "Confirm Location",
                        color = ZenithColors.Background,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}