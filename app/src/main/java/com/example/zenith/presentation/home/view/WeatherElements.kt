package com.example.zenith.presentation.home.view

import androidx.annotation.RawRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.zenith.R
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.DailyForecast
import com.example.zenith.data.model.HourlyForecast
import com.example.zenith.utils.StringHelper

@RawRes
fun iconCodeToLottie(iconCode: String): Int {
    return when {
        iconCode.startsWith("01") && iconCode.endsWith("d") -> R.raw.weather_sunny
        iconCode.startsWith("01") && iconCode.endsWith("n") -> R.raw.weather_night
        iconCode.startsWith("02")                           -> R.raw.weather_partly_cloudy
        iconCode.startsWith("03") || iconCode.startsWith("04") -> R.raw.weather_cloudy
        iconCode.startsWith("09")                           -> R.raw.weather_drizzle
        iconCode.startsWith("10")                           -> R.raw.weather_rainy
        iconCode.startsWith("11")                           -> R.raw.weather_thunder
        iconCode.startsWith("13")                           -> R.raw.weather_snow
        iconCode.startsWith("50")                           -> R.raw.weather_mist
        else                                                -> R.raw.weather_sunny
    }
}

@Composable
fun WeatherLottieIcon(
    iconCode: String,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(iconCodeToLottie(iconCode))
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

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
    val context = LocalContext.current
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

                WeatherLottieIcon(
                    iconCode = data.icon,
                    modifier = Modifier.size(130.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickStat(Icons.Rounded.Air, data.windSpeed, StringHelper.getString(context, R.string.wind_label, data.isArabic), isDay)
                QuickStat(Icons.Rounded.Cloud, "${data.clouds}%", StringHelper.getString(context, R.string.clouds_label, data.isArabic), isDay)
                QuickStat(Icons.Rounded.Opacity, "${data.humidity}", StringHelper.getString(context, R.string.humidity_label, data.isArabic), isDay)
                QuickStat(Icons.Rounded.Compress, "${data.pressure} hPa", StringHelper.getString(context, R.string.pressure_label, data.isArabic), isDay)
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
fun HourlyForecastRow(data: List<HourlyForecast>, isDay: Boolean, isArabic: Boolean = false) {
    val context = LocalContext.current
    Column {
        SectionTitle(StringHelper.getString(context, R.string.hourly_forecast_title, isArabic), isDay)
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
                    Text(
                        item.time,
                        color = if (isDay) Color.Black else Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                    WeatherLottieIcon(
                        iconCode = item.icon,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        item.temp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDay) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastList(data: List<DailyForecast>, isDay: Boolean, isArabic: Boolean = false) {
    val context = LocalContext.current
    Column {
        SectionTitle(StringHelper.getString(context, R.string.next_days_title, isArabic), isDay)
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(isDay, 28.dp)
                .padding(vertical = 8.dp)
        ) {
            data.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.day,
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        color = if (isDay) Color.Black else Color.White
                    )
                    WeatherLottieIcon(
                        iconCode = item.icon,
                        modifier = Modifier
                            .size(36.dp)
                            .weight(0.8f)
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.highTemp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDay) Color.Black else Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            item.lowTemp,
                            color = (if (isDay) Color.Black else Color.White).copy(alpha = 0.5f)
                        )
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
            val topColor = lerp(Color(0xFF050B18), Color(0xFF1565C0), animatedProgress)
            val bottomColor = lerp(Color(0xFF0A1628), Color(0xFF1E88E5), animatedProgress)
            drawRect(brush = Brush.verticalGradient(listOf(topColor, bottomColor)))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, end = 30.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            WeatherLottieIcon(
                iconCode = if (isDay) "01d" else "01n",
                modifier = Modifier
                    .size(110.dp)
                    .graphicsLayer { alpha = 0.35f }
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
