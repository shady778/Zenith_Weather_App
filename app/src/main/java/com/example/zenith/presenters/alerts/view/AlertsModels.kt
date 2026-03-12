package com.example.zenith.presenters.alerts.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.zenith.R
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.utils.StringHelper
import android.content.Context
import java.util.UUID

enum class AlertType(val labelRes: Int, val icon: ImageVector) {
    ALARM(R.string.mode_alarm, Icons.Rounded.Alarm),
    NOTIFICATION(R.string.mode_notif, Icons.Rounded.NotificationsActive);

    fun getLabel(context: Context, isArabic: Boolean) = StringHelper.getString(context, labelRes, isArabic)
}

enum class RepeatMode(val labelRes: Int) {
    ONCE(R.string.repeat_once),
    EVERY_DAY(R.string.repeat_daily),
    WEEKDAYS(R.string.repeat_weekdays),
    WEEKENDS(R.string.repeat_weekends);

    fun getLabel(context: Context, isArabic: Boolean) = StringHelper.getString(context, labelRes, isArabic)
}

enum class WeatherTrigger(val labelRes: Int, val icon: ImageVector, val color: Color, val unit: String = "") {
    ANY(R.string.trigger_any, Icons.Rounded.Cloud, Color(0xFF94A3B8)),
    TEMPERATURE(R.string.trigger_temp, Icons.Rounded.Thermostat, Color(0xFFFF5722), "°"),
    WIND_SPEED(R.string.trigger_wind, Icons.Rounded.Air, Color(0xFF03A9F4), " m/s"),
    RAIN(R.string.trigger_rain, Icons.Rounded.Umbrella, Color(0xFF2196F3), "%"),
    STORM(R.string.trigger_storm, Icons.Rounded.Thunderstorm, Color(0xFF9C27B0)),
    CLEAR(R.string.trigger_clear, Icons.Rounded.WbSunny, Color(0xFFFFC107)),
    SNOW(R.string.trigger_snow, Icons.Rounded.AcUnit, Color(0xFFE3F2FD)),
    CLOUDS(R.string.trigger_clouds, Icons.Rounded.CloudQueue, Color(0xFFB0BEC5), "%");

    fun getLabel(context: Context, isArabic: Boolean) = StringHelper.getString(context, labelRes, isArabic)
}

fun AlertEntity.getFormattedTime(context: Context, isArabic: Boolean): String {
    val h = if (hour % 12 == 0) 12 else hour % 12
    val m = minute.toString().padStart(2, '0')
    val period = if (hour < 12) {
        if (isArabic) "ص" else "AM"
    } else {
        if (isArabic) "م" else "PM"
    }
    return "$h:$m $period"
}
