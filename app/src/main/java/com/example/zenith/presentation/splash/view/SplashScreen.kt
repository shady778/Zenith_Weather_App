package com.example.zenith.presentation.splash.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.zenith.R
import com.example.zenith.presentation.splash.viewmodel.SplashState
import com.example.zenith.presentation.splash.viewmodel.SplashViewModel

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val state by viewModel.state.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val starProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stars"
    )
    val sunOrbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunOrbit"
    )

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_cloud_logo)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B1628), Color(0xFF162D50), Color(0xFF1A3A5C))
                )
            )
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val starSeeds = listOf(
                0.1f to 0.15f, 0.85f to 0.1f, 0.3f to 0.25f,
                0.7f to 0.2f, 0.15f to 0.4f, 0.9f to 0.35f,
                0.5f to 0.08f, 0.65f to 0.45f, 0.25f to 0.5f,
                0.45f to 0.3f, 0.8f to 0.48f, 0.05f to 0.55f,
                0.55f to 0.12f, 0.35f to 0.42f, 0.95f to 0.25f
            )
            starSeeds.forEachIndexed { i, (xFrac, yFrac) ->
                val phase = (starProgress + i * 0.07f) % 1f
                val alpha = (kotlin.math.sin(phase * Math.PI * 2).toFloat() * 0.5f + 0.5f) * 0.6f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = if (i % 3 == 0) 2.5f else 1.5f,
                    center = Offset(size.width * xFrac, size.height * yFrac)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7).copy(alpha = glowAlpha),
                                Color.Transparent
                            ),
                            radius = size.minDimension / 2
                        ),
                        radius = size.minDimension / 2
                    )
                }

                Canvas(modifier = Modifier.size(280.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val orbitRadiusX = size.width * 0.42f
                    val orbitRadiusY = size.height * 0.36f
                    val angleRad = Math.toRadians(sunOrbitAngle.toDouble())
                    val sunX = center.x + (orbitRadiusX * kotlin.math.cos(angleRad)).toFloat()
                    val sunY = center.y + (orbitRadiusY * kotlin.math.sin(angleRad)).toFloat()
                    val sunCenter = Offset(sunX, sunY)
                    val sunRadius = 16f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD54F).copy(alpha = 0.4f),
                                Color(0xFFFFE082).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = sunCenter,
                            radius = sunRadius * 5f
                        ),
                        radius = sunRadius * 5f,
                        center = sunCenter
                    )

                    for (i in 0 until 8) {
                        val rayAngle = Math.toRadians((i * 45f + sunOrbitAngle * 2f).toDouble())
                        val rayStart = Offset(
                            sunCenter.x + (sunRadius * 1.6f * kotlin.math.cos(rayAngle)).toFloat(),
                            sunCenter.y + (sunRadius * 1.6f * kotlin.math.sin(rayAngle)).toFloat()
                        )
                        val rayEnd = Offset(
                            sunCenter.x + (sunRadius * 3f * kotlin.math.cos(rayAngle)).toFloat(),
                            sunCenter.y + (sunRadius * 3f * kotlin.math.sin(rayAngle)).toFloat()
                        )
                        drawLine(
                            color = Color(0xFFFFD54F).copy(alpha = 0.6f),
                            start = rayStart,
                            end = rayEnd,
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Round
                        )
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF176), Color(0xFFFBC02D)),
                            center = sunCenter,
                            radius = sunRadius
                        ),
                        radius = sunRadius,
                        center = sunCenter
                    )
                }

                LottieAnimation(
                    composition = composition,
                    progress = { lottieProgress },
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer { translationY = floatY }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ZENITH",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Weather at its finest",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF64B5F6).copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            val statusText = when (state) {
                is SplashState.RequestingPermissions -> "Requesting Permissions..."
                is SplashState.CheckingLocationSettings -> "Enabling Location..."
                is SplashState.LoadingData -> "Fetching Weather..."
                is SplashState.Error -> (state as SplashState.Error).message
                else -> "Preparing..."
            }

            if (state !is SplashState.Error) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF4FC3F7),
                    strokeWidth = 2.dp,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (state is SplashState.Error) Color(0xFFFF5252) else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}
