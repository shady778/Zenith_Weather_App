package com.example.zenith.presenters.favorites.view

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(20.dp)
                        .background(ZenithColors.SurfaceGlass, CircleShape)
                        .border(1.dp, ZenithColors.BorderGlass, CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White)
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