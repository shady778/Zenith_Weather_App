package com.example.zenith.presenters.alerts.logic

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.zenith.R
import com.example.zenith.data.datasource.local.database.LocalDataSource
import com.example.zenith.data.location.LocationProvider
import com.example.zenith.data.datasource.remote.WeatherRemoteDataSource
import com.example.zenith.data.db.AppDatabase
import com.example.zenith.data.local.datastore.SettingsDataStore
import com.example.zenith.data.network.RetrofitInstance
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.presenters.alerts.view.AlertType
import com.example.zenith.presenters.alerts.view.WeatherTrigger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import java.util.Locale

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getString("ALERT_ID") ?: return Result.failure()

        val database = AppDatabase.getDatabase(applicationContext)
        val alertDao = database.alertDao()
        val weatherDao = database.weatherDao()
        val favoriteDao = database.favoriteCityDao()
        val apiService = RetrofitInstance.apiService
        val remoteDataSource = WeatherRemoteDataSource(apiService)
        val locationProvider = LocationProvider(applicationContext)
        val localDataSource = LocalDataSource(favoriteDao,alertDao,weatherDao)
        val settingsDataStore = SettingsDataStore(applicationContext)
        val weatherRepository = WeatherRepository(
            remoteDataSource,
            locationProvider,
            localDataSource,
            settingsDataStore,
        )

        val notificationHelper = NotificationHelper(applicationContext)

        val alert = weatherRepository.getAlertById(alertId) ?: return Result.failure()
        if (!alert.isEnabled) return Result.success()

        try {
            val settings = settingsDataStore.settingsFlow.first()
            val coord = try {
                if (settings.locProvider == "MANUAL") {
                    Pair(settings.manualLat, settings.manualLon)
                } else {
                    withTimeout(5000) {
                        locationProvider.fetchLocation().first()
                    }
                }
            } catch (e: Exception) {
                if (settings.locProvider == "MANUAL") Pair(settings.manualLat, settings.manualLon)
                else return Result.retry()
            }

            val lang = if (settings.language == "ARABIC") "ar" else "en"
            val units = if (settings.tempUnit == "FAHRENHEIT") "imperial" else "metric"
            val weather = weatherRepository.fetchCurrent(coord.first, coord.second, units, lang)

            val shouldNotify = when (alert.trigger) {
                WeatherTrigger.ANY -> true
                WeatherTrigger.TEMPERATURE -> {
                    val currentTemp = weather.main.temp.toInt()
                    alert.triggerValue?.let { currentTemp >= it } ?: true
                }
                WeatherTrigger.WIND_SPEED -> {
                    val currentWind = weather.wind.speed.toInt()
                    alert.triggerValue?.let { currentWind >= it } ?: true
                }
                WeatherTrigger.RAIN -> weather.weather.any { it.main.equals("Rain", ignoreCase = true) }
                WeatherTrigger.STORM -> weather.weather.any { it.main.equals("Thunderstorm", ignoreCase = true) }
                WeatherTrigger.CLEAR -> weather.weather.any { it.main.equals("Clear", ignoreCase = true) }
                WeatherTrigger.SNOW -> weather.weather.any { it.main.equals("Snow", ignoreCase = true) }
                WeatherTrigger.CLOUDS -> {
                    val cloudiness = weather.clouds.all
                    alert.triggerValue?.let { cloudiness >= it } ?: true
                }
            }

            if (shouldNotify) {
                val isArabic = settings.language == "ARABIC"
                val localizedContext = getLocalizedContext(applicationContext, settings.language)
                
                val rawDesc = weather.weather.firstOrNull()?.description ?: ""
                val weatherDesc = translateWeather(localizedContext, rawDesc)
                val triggerName = alert.trigger.getLabel(applicationContext, isArabic)
                
                val triggerReading = when(alert.trigger) {
                    WeatherTrigger.TEMPERATURE -> localizedContext.getString(R.string.temperature_format, weather.main.temp.toString())
                    WeatherTrigger.WIND_SPEED -> {
                        val windUnit = localizedContext.getString(R.string.wind_unit_kmh)
                        localizedContext.getString(R.string.wind_speed_format, weather.wind.speed.toString(), windUnit)
                    }
                    WeatherTrigger.RAIN -> localizedContext.getString(R.string.rain_detected)
                    WeatherTrigger.CLOUDS -> localizedContext.getString(R.string.cloudiness_format, weather.clouds.all)
                    else -> weatherDesc
                }

                val modeLabel = alert.type.getLabel(applicationContext, isArabic)
                val title = localizedContext.getString(R.string.notification_title_format, modeLabel, alert.label)
                val weatherLabel = localizedContext.getString(R.string.weather_label_format, weatherDesc, "${weather.main.temp}°")

                val isAlarm = alert.type == AlertType.ALARM

                var alarmIntent: Intent? = null
                val fullScreenIntent = if (isAlarm) {
                    alarmIntent = Intent(applicationContext, AlarmActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("ALERT_ID", alert.id)
                        putExtra("ALERT_LABEL", alert.label)
                        putExtra("ALERT_TRIGGER", triggerName)
                        putExtra("TRIGGER_READING", triggerReading)
                        putExtra("WEATHER_DESC", weatherDesc)
                        putExtra("WEATHER_TEMP", "${weather.main.temp}°")
                        putExtra("WEATHER_ICON", weather.weather.firstOrNull()?.icon ?: "01d")
                        putExtra("REPEAT_MODE", alert.repeat.name)
                        putExtra("IS_ARABIC", isArabic)
                    }
                    PendingIntent.getActivity(
                        applicationContext,
                        alert.id.hashCode(),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                } else null

                if (isAlarm && alarmIntent != null) {
                    try {
                        applicationContext.startActivity(alarmIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                notificationHelper.showNotification(title, weatherLabel, fullScreenIntent)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun getLocalizedContext(context: Context, language: String): Context {
        val locale = if (language == "ARABIC") Locale.forLanguageTag("ar") else Locale.forLanguageTag("en")
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun translateWeather(context: Context, desc: String): String {
        val resId = when (desc.lowercase()) {
            "clear sky" -> R.string.weather_clear_sky
            "few clouds" -> R.string.weather_few_clouds
            "scattered clouds" -> R.string.weather_scattered_clouds
            "broken clouds" -> R.string.weather_broken_clouds
            "overcast clouds" -> R.string.weather_overcast_clouds
            "shower rain" -> R.string.weather_shower_rain
            "rain" -> R.string.weather_rain
            "thunderstorm" -> R.string.weather_thunderstorm
            "snow" -> R.string.weather_snow
            "mist" -> R.string.weather_mist
            "smoke" -> R.string.weather_smoke
            "haze" -> R.string.weather_haze
            "dust" -> R.string.weather_dust
            "fog" -> R.string.weather_fog
            "sand" -> R.string.weather_sand
            "ash" -> R.string.weather_ash
            "squall" -> R.string.weather_squall
            "tornado" -> R.string.weather_tornado
            "light rain" -> R.string.weather_light_rain
            "moderate rain" -> R.string.weather_moderate_rain
            "heavy intensity rain" -> R.string.weather_heavy_intensity_rain
            else -> null
        }
        return resId?.let { context.getString(it) } ?: desc
    }
}
