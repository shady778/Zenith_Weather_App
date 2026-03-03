package com.example.weatherappfinal.presenters.home.view.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.weatherappfinal.data.model.DailyForecast
import com.example.weatherappfinal.data.model.HourlyForecast
import com.example.weatherappfinal.data.model.WeatherData

fun Modifier.glassCard(isDay: Boolean, cornerRadius: Dp): Modifier = this.then(
    Modifier
        .background(
            color = if (isDay) Color(0x66FFFFFF) else Color(0x33AAB8FF),
            shape = RoundedCornerShape(cornerRadius)
        )
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(cornerRadius)
        )
        .clip(RoundedCornerShape(cornerRadius))
)

@Composable
fun WeatherHeader(data: WeatherData, isDay: Boolean) {
    val textColor = if (isDay) Color.Black.copy(0.8f) else Color.White

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = if (isDay) Color(0xFFFF5722) else Color(0xFF00BCD4),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${data.city}, ${data.country}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }
        }

        Text(
            text = data.localTime,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun MainWeatherCard(data: WeatherData, isDay: Boolean) {
    val textColor = if (isDay) Color.Black.copy(0.8f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(isDay, cornerRadius = 32.dp)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = data.temperature,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 85.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor
                        )
                    )
                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = textColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${data.hourlyForecast.firstOrNull()?.icon ?: "01d"}@4x.png",
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickStat(Icons.Rounded.Air, "${data.windSpeed} m/s", "Wind", isDay)
                QuickStat(Icons.Rounded.Cloud, "${data.clouds}%", "Clouds", isDay)
                QuickStat(Icons.Rounded.Opacity, "${data.humidity}%", "Humidity", isDay)
                QuickStat(Icons.Rounded.Compress, "${data.pressure} hPa", "Pressure", isDay)
            }
        }
    }
}

@Composable
fun QuickStat(icon: ImageVector, value: String, label: String, isDay: Boolean) {
    val textColor = if (isDay) Color.Black.copy(0.8f) else Color.White
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = textColor))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = textColor.copy(0.6f)))
    }
}
@Composable
fun HourlyForecastRow(data: List<HourlyForecast>, isDay: Boolean) {
    Column {
        SectionTitle("Hourly Forecast", isDay)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(data.size) { index ->
                val item = data[index]
                Column(
                    modifier = Modifier
                        .width(70.dp)
                        .glassCard(isDay, 20.dp)
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(item.time, color = if(isDay) Color.Black else Color.White, style = MaterialTheme.typography.labelMedium)
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${item.icon}@2x.png",
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(item.temp, fontWeight = FontWeight.Bold, color = if(isDay) Color.Black else Color.White)
                }
            }
        }
    }
}

@Composable
fun DailyForecastList(data: List<DailyForecast>, isDay: Boolean) {
    Column {
        SectionTitle("Next Days", isDay)
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(isDay, 28.dp)
                .padding(vertical = 8.dp)
        ) {
            data.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.day, modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, color = if(isDay) Color.Black else Color.White)
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${item.icon}@2x.png",
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).weight(0.8f)
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.highTemp, fontWeight = FontWeight.Bold, color = if(isDay) Color.Black else Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(item.lowTemp, color = (if(isDay) Color.Black else Color.White).copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, isDay: Boolean) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isDay) Color.Black.copy(0.7f) else Color.White.copy(0.9f)
        ),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun WeatherBackground(isDay: Boolean, content: @Composable BoxScope.() -> Unit) {
    val animatedProgress by animateFloatAsState(targetValue = if (isDay) 1f else 0f, animationSpec = tween(1500), label = "")

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val topColor = lerp(Color(0xFF0D1B2A), Color(0xFF4A90E2), animatedProgress)
            val bottomColor = lerp(Color(0xFF1B263B), Color(0xFF87CEEB), animatedProgress)
            drawRect(brush = Brush.verticalGradient(listOf(topColor, bottomColor)))
        }

        // Sun or Moon Icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, end = 40.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            val iconScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = ""
            )

            Icon(
                imageVector = if (isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                        alpha = 0.3f
                    },
                tint = if (isDay) Color(0xFFFFD600) else Color(0xFFE0E0E0)
            )
        }

        content()
    }
}

@Composable
fun RainAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(80) { i ->
            val x = (i * 45f) % size.width
            val y = ((i * 100f) + (progress * size.height)) % size.height
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = Offset(x, y),
                end = Offset(x - 2f, y + 30f),
                strokeWidth = 2f
            )
        }
    }
}

fun lerp(start: Color, stop: Color, fraction: Float): Color = Color(
    red = start.red + (stop.red - start.red) * fraction,
    green = start.green + (stop.green - start.green) * fraction,
    blue = start.blue + (stop.blue - start.blue) * fraction,
    alpha = start.alpha + (stop.alpha - start.alpha) * fraction
)