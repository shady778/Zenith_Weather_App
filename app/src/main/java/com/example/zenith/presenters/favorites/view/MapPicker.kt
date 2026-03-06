package com.example.zenith.presenters.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.zenith.ui.theme.ZenithColors
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

@Composable
fun MapPicker(onDismiss: () -> Unit, onLocationSelected: (String, String) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f))) {
        Card(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = ZenithColors.Background)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(10.0)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(20.dp)
                        .background(ZenithColors.SurfaceGlass, CircleShape)
                        .border(1.dp, ZenithColors.BorderGlass, CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
                }

                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = ZenithColors.Cyan,
                    modifier = Modifier.size(48.dp).align(Alignment.Center)
                )

                Button(
                    onClick = { onLocationSelected("Selected Point", "??") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .height(56.dp)
                        .padding(horizontal = 40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZenithColors.Cyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Confirm Location", color = ZenithColors.Background, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}