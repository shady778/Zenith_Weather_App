package com.example.zenith.presentation.alerts.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getStringExtra("ALERT_ID") ?: return

        val inputData = Data.Builder()
            .putString("ALERT_ID", alertId)
            .build()

        val checkWeatherWork = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(checkWeatherWork)
    }
}