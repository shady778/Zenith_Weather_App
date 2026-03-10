package com.example.zenith.data.db

import androidx.room.TypeConverter
import com.example.zenith.presenters.alerts.view.AlertType
import com.example.zenith.presenters.alerts.view.RepeatMode
import com.example.zenith.presenters.alerts.view.WeatherTrigger

class Converters {
    @TypeConverter
    fun fromAlertType(value: AlertType) = value.name

    @TypeConverter
    fun toAlertType(value: String) = AlertType.valueOf(value)

    @TypeConverter
    fun fromRepeatMode(value: RepeatMode) = value.name

    @TypeConverter
    fun toRepeatMode(value: String) = RepeatMode.valueOf(value)

    @TypeConverter
    fun fromWeatherTrigger(value: WeatherTrigger) = value.name

    @TypeConverter
    fun toWeatherTrigger(value: String) = WeatherTrigger.valueOf(value)
}