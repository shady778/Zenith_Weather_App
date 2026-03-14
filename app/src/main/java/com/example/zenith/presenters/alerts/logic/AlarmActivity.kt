package com.example.zenith.presenters.alerts.logic

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.zenith.R
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.ui.theme.ZenithTheme
import com.example.zenith.data.db.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AlarmActivity : ComponentActivity() {
    private var ringtone: android.media.Ringtone? = null
    private var vibrator: android.os.Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startAlarmMedia()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as android.app.KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        
        enableEdgeToEdge()
        
        val alertId = intent.getStringExtra("ALERT_ID")
        val alertLabel = intent.getStringExtra("ALERT_LABEL") ?: "Weather Update"
        val alertTrigger = intent.getStringExtra("ALERT_TRIGGER") ?: "Alert"
        val triggerReading = intent.getStringExtra("TRIGGER_READING") ?: ""
        val weatherDesc = intent.getStringExtra("WEATHER_DESC") ?: ""
        val weatherTemp = intent.getStringExtra("WEATHER_TEMP") ?: ""
        val weatherIcon = intent.getStringExtra("WEATHER_ICON") ?: "01d"
        val repeatMode = intent.getStringExtra("REPEAT_MODE") ?: "EVERY_DAY"
        val isArabic = intent.getBooleanExtra("IS_ARABIC", false)

        setContent {
            ZenithTheme {
                AlarmScreen(
                    label = alertLabel,
                    trigger = alertTrigger,
                    reading = triggerReading,
                    desc = weatherDesc,
                    temp = weatherTemp,
                    icon = weatherIcon,
                    isArabic = isArabic,
                    onDismiss = { 
                        stopAlarmMedia()
                        if (repeatMode == com.example.zenith.presenters.alerts.view.RepeatMode.ONCE.name && alertId != null) {
                            lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                val db = AppDatabase.getDatabase(applicationContext)
                                val alert = db.alertDao().getAlertById(alertId)
                                if (alert != null) {
                                    db.alertDao().deleteAlert(alert)
                                }
                            }
                        }
                        finish() 
                    }
                )
            }
        }
    }

    private fun startAlarmMedia() {
        try {
            val alertUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
            ringtone = android.media.RingtoneManager.getRingtone(applicationContext, alertUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true
            }
            ringtone?.play()
            vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
            if (vibrator?.hasVibrator() == true) {
                val pattern = longArrayOf(0, 1000, 500)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmMedia() {
        ringtone?.stop()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopAlarmMedia()
        super.onDestroy()
    }
}

@Composable
fun AlarmScreen(
    label: String, 
    trigger: String, 
    reading: String, 
    desc: String, 
    temp: String, 
    icon: String, 
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val lottieRes = remember(icon) {
        when (icon) {
            "01d" -> R.raw.weather_sunny
            "01n" -> R.raw.weather_night
            "02d", "03d", "04d" -> R.raw.weather_partly_cloudy
            "02n", "03n", "04n" -> R.raw.weather_cloudy
            "09d", "09n", "10d" -> R.raw.weather_rainy
            "10n" -> R.raw.weather_night_rain_dark
            "11d", "11n" -> R.raw.weather_thunder
            "13d", "13n" -> R.raw.weather_snow
            "50d", "50n" -> R.raw.weather_mist
            else -> R.raw.weather_cloudy
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B).copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale)
                            .background(ZenithColors.Cyan.copy(0.1f), CircleShape)
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(100.dp)
                    )
                }

                Text(
                    text = trigger.uppercase(),
                    color = ZenithColors.Cyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Surface(
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val mainDisplay = reading.ifBlank { temp }
                            Text(mainDisplay, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                            Text(desc, color = Color.White.copy(0.6f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ZenithColors.Cyan),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Rounded.Close, null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isArabic) "إغلاق التنبيه" else "DISMISS ALERT", 
                        color = Color.Black, 
                        fontWeight = FontWeight.Black, 
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
