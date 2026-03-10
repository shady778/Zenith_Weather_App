package com.example.zenith.data.datasource.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zenith.presenters.alerts.view.AlertType
import com.example.zenith.presenters.alerts.view.RepeatMode
import com.example.zenith.presenters.alerts.view.WeatherTrigger

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey
    val id: String,
    val hour: Int,
    val minute: Int,
    val type: AlertType,
    val trigger: WeatherTrigger,
    val triggerValue: Int?,
    val repeat: RepeatMode,
    val isEnabled: Boolean,
    val label: String
)