package com.example.zenith.data.db

import androidx.room.TypeConverter
import com.example.zenith.data.model.WeatherData
import com.google.gson.Gson
import com.example.zenith.presentation.alerts.view.AlertType
import com.example.zenith.presentation.alerts.view.RepeatMode
import com.example.zenith.presentation.alerts.view.WeatherTrigger

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherData(data: WeatherData?): String? = data?.let { gson.toJson(it) }

    @TypeConverter
    fun toWeatherData(value: String?): WeatherData? = value?.let { gson.fromJson(it, WeatherData::class.java) }

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