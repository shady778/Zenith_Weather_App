package com.example.zenith.presentation.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.presentation.home.view.WeatherBackground
import com.example.zenith.presentation.settings.viewmodel.SettingsViewModel
import com.example.zenith.data.model.localizeNumbers
import androidx.compose.ui.platform.LocalContext
import com.example.zenith.R
import com.example.zenith.utils.StringHelper
import com.example.zenith.ui.components.ZenithTopBar
import com.example.zenith.presentation.favorites.view.MapPicker

@Composable
fun SettingsScreen(
    isDay: Boolean,
    viewModel: SettingsViewModel,
    onLocationChanged: () -> Unit = {}
) {
    val settings by viewModel.settingsState.collectAsState()
    val isArabic = settings.language == "ARABIC"
    var showMapPicker by remember { mutableStateOf(false) }

    WeatherBackground(isDay = isDay) {
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    val context = LocalContext.current
                    ZenithTopBar(
                        title = StringHelper.getString(context, R.string.settings_title, isArabic),
                        subtitle = StringHelper.getString(context, R.string.settings_subtitle, isArabic)
                    )
                }

                item {
                    val context = LocalContext.current
                    SettingsSection(title = StringHelper.getString(context, R.string.units_section, isArabic)) {
                        SettingsRow(
                            icon = Icons.Rounded.Thermostat,
                            iconTint = Color(0xFFFF8A65),
                            iconBackground = Color(0x1FFF8A65),
                            title = StringHelper.getString(context, R.string.temp_label, isArabic),
                            subtitle = StringHelper.getString(context, R.string.temp_currently, isArabic).format(
                                when (settings.tempUnit) {
                                    "CELSIUS" -> StringHelper.getString(context, R.string.celsius, isArabic)
                                    "FAHRENHEIT" -> StringHelper.getString(context, R.string.fahrenheit, isArabic)
                                    "KELVIN" -> StringHelper.getString(context, R.string.kelvin, isArabic)
                                    else -> settings.tempUnit
                                }
                            ),
                            showDivider = true
                        ) {
                            SettingsToggleGroup(
                                options = TemperatureUnit.entries.toList(),
                                selected = TemperatureUnit.valueOf(settings.tempUnit),
                                onSelect = { viewModel.updateSettings(settings.copy(tempUnit = it.name)) },
                                labelOf = { it.symbol.localizeNumbers(isArabic) },
                                modifier = Modifier.width(140.dp) // Reduced width
                            )
                        }

                        SettingsRow(
                            icon = Icons.Rounded.Air,
                            iconTint = Color(0xFF81C784),
                            iconBackground = Color(0x1481C784),
                            title = StringHelper.getString(context, R.string.wind_speed_label, isArabic),
                            subtitle = StringHelper.getString(context, R.string.wind_currently, isArabic).format(
                                when (settings.windUnit) {
                                    "MS" -> StringHelper.getString(context, R.string.ms, isArabic)
                                    "MPH" -> StringHelper.getString(context, R.string.mph, isArabic)
                                    else -> settings.windUnit
                                }
                            )
                        ) {
                            SettingsToggleGroup(
                                options = WindUnit.entries.toList(),
                                selected = WindUnit.valueOf(settings.windUnit),
                                onSelect = { viewModel.updateSettings(settings.copy(windUnit = it.name)) },
                                labelOf = { 
                                    when(it) {
                                        WindUnit.MS -> StringHelper.getString(context, R.string.ms, isArabic)
                                        WindUnit.MPH -> StringHelper.getString(context, R.string.mph, isArabic)
                                    }
                                },
                                modifier = Modifier.width(120.dp) // Reduced width
                            )
                        }
                    }
                }

                item {
                    val context = LocalContext.current
                    SettingsSection(title = StringHelper.getString(context, R.string.preferences_section, isArabic)) {
                        SettingsRow(
                            icon = Icons.Rounded.Language,
                            iconTint = Color(0xFF80CBC4),
                            iconBackground = Color(0x1480CBC4),
                            title = StringHelper.getString(context, R.string.language_label, isArabic),
                            subtitle = when (settings.language) {
                                "ARABIC" -> StringHelper.getString(context, R.string.arabic, isArabic)
                                else -> StringHelper.getString(context, R.string.english, isArabic)
                            },
                            showDivider = true
                        ) {
                            SettingsToggleGroup(
                                options = AppLanguage.entries.toList(),
                                selected = AppLanguage.valueOf(settings.language),
                                onSelect = { viewModel.updateSettings(settings.copy(language = it.name)) },
                                labelOf = { it.label },
                                modifier = Modifier.width(130.dp)
                            )
                        }

                        SettingsRow(
                            icon = Icons.Rounded.NotificationsActive,
                            iconTint = ZenithColors.Cyan,
                            iconBackground = ZenithColors.Cyan.copy(0.1f),
                            title = StringHelper.getString(context, R.string.notifications_label, isArabic),
                            subtitle = StringHelper.getString(context, R.string.notifs_subtitle, isArabic)
                        ) {
                            Switch(
                                checked = settings.notifsEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(notifsEnabled = it)) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ZenithColors.Cyan,
                                    checkedTrackColor = ZenithColors.Cyan.copy(0.3f)
                                )
                            )
                        }
                    }
                }

                item {
                    val context = LocalContext.current
                    SettingsSection(title = StringHelper.getString(context, R.string.location_section, isArabic)) {
                        SettingsRow(
                            icon = Icons.Rounded.LocationOn,
                            iconTint = Color(0xFF80D8FF),
                            iconBackground = Color(0x1480D8FF),
                            title = StringHelper.getString(context, R.string.source_label, isArabic),
                            subtitle = when (settings.locProvider) {
                                "GPS" -> StringHelper.getString(context, R.string.gps, isArabic)
                                "MANUAL" -> StringHelper.getString(context, R.string.manual, isArabic)
                                else -> settings.locProvider
                            }
                        ) {
                            SettingsToggleGroup(
                                options = LocationProvider.entries.toList(),
                                selected = LocationProvider.valueOf(settings.locProvider),
                                onSelect = { selected ->
                                    if (selected == LocationProvider.MANUAL) {
                                        showMapPicker = true
                                    } else {
                                        viewModel.updateSettings(settings.copy(locProvider = "GPS"))
                                        onLocationChanged()
                                    }
                                },
                                labelOf = { 
                                    when(it) {
                                        LocationProvider.GPS -> StringHelper.getString(context, R.string.gps, isArabic)
                                        LocationProvider.MANUAL -> StringHelper.getString(context, R.string.manual, isArabic)
                                    }
                                },
                                modifier = Modifier.width(120.dp) // Reduced width
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMapPicker) {
        MapPicker(
            initialLat = if (settings.manualLat != 0.0) settings.manualLat else 30.0444,
            initialLon = if (settings.manualLon != 0.0) settings.manualLon else 31.2357,
            isArabic = isArabic,
            onDismiss = { showMapPicker = false },
            onLocationSelected = { _, _, lat, lon ->
                viewModel.updateSettings(
                    settings.copy(
                        locProvider = "MANUAL",
                        manualLat = lat,
                        manualLon = lon
                    )
                )
                onLocationChanged()
            }
        )
    }
}
