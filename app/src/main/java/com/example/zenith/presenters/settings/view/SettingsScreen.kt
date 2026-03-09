package com.example.zenith.presenters.settings.view

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
import com.example.zenith.presenters.home.ui.WeatherBackground
import com.example.zenith.presenters.settings.viewmodel.SettingsViewModel
import com.example.zenith.ui.components.ZenithTopBar
import com.example.zenith.presenters.favorites.view.MapPicker
import com.example.zenith.data.model.localizeNumbers

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
                    ZenithTopBar(
                        title = if (isArabic) "الإعدادات" else "Settings",
                        subtitle = if (isArabic) "خصّص تجربتك في Zenith" else "Customize your Zenith experience"
                    )
                }

                item {
                    SettingsSection(title = if (isArabic) "الوحدات" else "Units") {
                        SettingsRow(
                            icon = Icons.Rounded.Thermostat,
                            iconTint = Color(0xFFFF8A65),
                            iconBackground = Color(0x1FFF8A65),
                            title = if (isArabic) "درجة الحرارة" else "Temperature",
                            subtitle = if (isArabic) "الحالي: ${
                                when (settings.tempUnit) {
                                    "CELSIUS" -> "مئوية"
                                    "FAHRENHEIT" -> "فهرنهايت"
                                    "KELVIN" -> "كلفن"
                                    else -> settings.tempUnit
                                }
                            }" else "Currently: ${settings.tempUnit}",
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
                            title = if (isArabic) "سرعة الرياح" else "Wind Speed",
                            subtitle = if (isArabic) "الحالي: ${
                                when (settings.windUnit) {
                                    "MS" -> "م/ث"
                                    "MPH" -> "ميل/س"
                                    else -> settings.windUnit
                                }
                            }" else "Currently: ${settings.windUnit}"
                        ) {
                            SettingsToggleGroup(
                                options = WindUnit.entries.toList(),
                                selected = WindUnit.valueOf(settings.windUnit),
                                onSelect = { viewModel.updateSettings(settings.copy(windUnit = it.name)) },
                                labelOf = { 
                                    if (isArabic) {
                                        when(it) {
                                            WindUnit.MS -> "م/ث"
                                            WindUnit.MPH -> "ميل/س"
                                        }
                                    } else it.label 
                                },
                                modifier = Modifier.width(120.dp) // Reduced width
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = if (isArabic) "التفضيلات" else "Preferences") {
                        SettingsRow(
                            icon = Icons.Rounded.Language,
                            iconTint = Color(0xFF80CBC4),
                            iconBackground = Color(0x1480CBC4),
                            title = if (isArabic) "اللغة" else "Language",
                            subtitle = if (isArabic) {
                                when (settings.language) {
                                    "ARABIC" -> "العربية"
                                    else -> "الإنجليزية"
                                }
                            } else settings.language,
                            showDivider = true
                        ) {
                            SettingsToggleGroup(
                                options = AppLanguage.entries.toList(),
                                selected = AppLanguage.valueOf(settings.language),
                                onSelect = { viewModel.updateSettings(settings.copy(language = it.name)) },
                                labelOf = { it.label },
                                modifier = Modifier.width(130.dp) // Reduced width
                            )
                        }

                        SettingsRow(
                            icon = Icons.Rounded.NotificationsActive,
                            iconTint = ZenithColors.Cyan,
                            iconBackground = ZenithColors.Cyan.copy(0.1f),
                            title = if (isArabic) "الإشعارات" else "Notifications",
                            subtitle = if (isArabic) "تحديثات الطقس اليومية" else "Daily weather updates"
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
                    SettingsSection(title = if (isArabic) "الموقع" else "Location") {
                        SettingsRow(
                            icon = Icons.Rounded.LocationOn,
                            iconTint = Color(0xFF80D8FF),
                            iconBackground = Color(0x1480D8FF),
                            title = if (isArabic) "المصدر" else "Source",
                            subtitle = if (isArabic) {
                                when (settings.locProvider) {
                                    "GPS" -> "GPS"
                                    "MANUAL" -> "يدوي"
                                    else -> settings.locProvider
                                }
                            } else settings.locProvider
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
                                    if (isArabic) {
                                        when(it) {
                                            LocationProvider.GPS -> "GPS"
                                            LocationProvider.MANUAL -> "يدوي"
                                        }
                                    } else it.label 
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
