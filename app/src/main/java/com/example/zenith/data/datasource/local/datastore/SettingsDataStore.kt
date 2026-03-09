package com.example.zenith.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "zenith_settings")

class SettingsDataStore(private val context: Context) {

    private val TEMP_UNIT = stringPreferencesKey("temp_unit")
    private val WIND_UNIT = stringPreferencesKey("wind_unit")
    private val LANG = stringPreferencesKey("language")
    private val LOC_PROVIDER = stringPreferencesKey("location_provider")
    private val NOTIFS_ENABLED = booleanPreferencesKey("notifs_enabled")
    private val DAILY_ALERTS = booleanPreferencesKey("daily_alerts")
    private val MANUAL_LAT = doublePreferencesKey("manual_lat")
    private val MANUAL_LON = doublePreferencesKey("manual_lon")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            tempUnit = prefs[TEMP_UNIT] ?: "CELSIUS",
            windUnit = prefs[WIND_UNIT] ?: "MS",
            language = prefs[LANG] ?: "ENGLISH",
            locProvider = prefs[LOC_PROVIDER] ?: "GPS",
            notifsEnabled = prefs[NOTIFS_ENABLED] ?: true,
            dailyAlerts = prefs[DAILY_ALERTS] ?: false,
            manualLat = prefs[MANUAL_LAT] ?: 0.0,
            manualLon = prefs[MANUAL_LON] ?: 0.0
        )
    }
    suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[TEMP_UNIT] = settings.tempUnit
            prefs[WIND_UNIT] = settings.windUnit
            prefs[LANG] = settings.language
            prefs[LOC_PROVIDER] = settings.locProvider
            prefs[NOTIFS_ENABLED] = settings.notifsEnabled
            prefs[DAILY_ALERTS] = settings.dailyAlerts
            prefs[MANUAL_LAT] = settings.manualLat
            prefs[MANUAL_LON] = settings.manualLon
        }
    }
}
data class UserSettings(
    val tempUnit: String,
    val windUnit: String,
    val language: String,
    val locProvider: String,
    val notifsEnabled: Boolean,
    val dailyAlerts: Boolean,
    val manualLat: Double = 0.0,
    val manualLon: Double = 0.0
)