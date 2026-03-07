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
import com.example.zenith.ui.components.ZenithTopBar

@Composable
fun SettingsScreen(isDay: Boolean) {
    var tempUnit by remember { mutableStateOf(TemperatureUnit.CELSIUS) }
    var windUnit by remember { mutableStateOf(WindUnit.MS) }
    var language by remember { mutableStateOf(AppLanguage.ENGLISH) }
    var locationProvider by remember { mutableStateOf(LocationProvider.GPS) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    WeatherBackground(isDay = isDay) {
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { ZenithTopBar(title = "Settings", subtitle = "Customize your Zenith experience") }

                item {
                    SettingsSection(title = "Temperature Unit") {
                        SettingsRow(
                            icon = Icons.Rounded.Thermostat,
                            iconTint = Color(0xFFFF8A65),
                            iconBackground = Color(0x1FFF8A65),
                            title = "Display Unit",
                            subtitle = "Currently: ${tempUnit.label} (${tempUnit.symbol})"
                        ) {
                            SettingsToggleGroup(
                                options = TemperatureUnit.entries.toList(),
                                selected = tempUnit,
                                onSelect = { tempUnit = it },
                                labelOf = { it.symbol },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = "Preferences") {
                        SettingsRow(
                            icon = Icons.Rounded.Language,
                            iconTint = Color(0xFF80CBC4),
                            iconBackground = Color(0x1480CBC4),
                            title = "Language",
                            subtitle = language.label,
                            showDivider = true
                        ) {
                            SettingsToggleGroup(
                                options = AppLanguage.entries.toList(),
                                selected = language,
                                onSelect = { language = it },
                                labelOf = { it.label },
                                modifier = Modifier.width(150.dp)
                            )
                        }

                        SettingsRow(
                            icon = Icons.Rounded.NotificationsActive,
                            iconTint = ZenithColors.Cyan,
                            iconBackground = ZenithColors.Cyan.copy(0.1f),
                            title = "Notifications",
                            subtitle = "Daily weather updates"
                        ) {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ZenithColors.Cyan,
                                    checkedTrackColor = ZenithColors.Cyan.copy(0.3f)
                                )
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = "Location") {
                        SettingsRow(
                            icon = Icons.Rounded.LocationOn,
                            iconTint = Color(0xFF80D8FF),
                            iconBackground = Color(0x1480D8FF),
                            title = "Source",
                            subtitle = locationProvider.label
                        ) {
                            SettingsToggleGroup(
                                options = LocationProvider.entries.toList(),
                                selected = locationProvider,
                                onSelect = { locationProvider = it },
                                labelOf = { it.label },
                                modifier = Modifier.width(140.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}