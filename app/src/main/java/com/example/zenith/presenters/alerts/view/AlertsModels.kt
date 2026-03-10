package com.example.zenith.presenters.alerts.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.zenith.data.datasource.local.database.AlertEntity
import java.util.UUID

enum class AlertType(val labelEn: String, val labelAr: String, val icon: ImageVector) {
    ALARM("Alarm", "منبه", Icons.Rounded.Alarm),
    NOTIFICATION("Notification", "إشعار", Icons.Rounded.NotificationsActive);

    fun getLabel(isArabic: Boolean) = if (isArabic) labelAr else labelEn
}

enum class RepeatMode(val labelEn: String, val labelAr: String) {
    ONCE("Once", "مرة واحدة"),
    EVERY_DAY("Every Day", "كل يوم"),
    WEEKDAYS("Weekdays", "أيام العمل"),
    WEEKENDS("Weekends", "عطلة الأسبوع");

    fun getLabel(isArabic: Boolean) = if (isArabic) labelAr else labelEn
}

enum class WeatherTrigger(val labelEn: String, val labelAr: String, val icon: ImageVector, val color: Color, val unit: String = "") {
    ANY("Any", "أي طقس", Icons.Rounded.Cloud, Color(0xFF94A3B8)),
    TEMPERATURE("Temp", "حرارة", Icons.Rounded.Thermostat, Color(0xFFFF5722), "°"),
    WIND_SPEED("Wind", "رياح", Icons.Rounded.Air, Color(0xFF03A9F4), " m/s"),
    RAIN("Rain", "مطر", Icons.Rounded.Umbrella, Color(0xFF2196F3), "%"),
    STORM("Storm", "عواصف", Icons.Rounded.Thunderstorm, Color(0xFF9C27B0)),
    CLEAR("Clear", "صافي", Icons.Rounded.WbSunny, Color(0xFFFFC107)),
    SNOW("Snow", "ثلج", Icons.Rounded.AcUnit, Color(0xFFE3F2FD)),

    CLOUDS("Clouds", "غيوم", Icons.Rounded.CloudQueue, Color(0xFFB0BEC5), "%");

    fun getLabel(isArabic: Boolean) = if (isArabic) labelAr else labelEn
}

fun AlertEntity.getFormattedTime(isArabic: Boolean): String {
    val h = if (hour % 12 == 0) 12 else hour % 12
    val m = minute.toString().padStart(2, '0')
    val period = if (isArabic) (if (hour < 12) "ص" else "م") else (if (hour < 12) "AM" else "PM")
    return "$h:$m $period"
}