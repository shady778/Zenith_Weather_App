package com.example.zenith.presenters.alerts.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.zenith.presenters.alerts.viewmodel.AlertViewModel
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.presenters.home.ui.WeatherBackground
import androidx.compose.ui.platform.LocalContext
import com.example.zenith.R
import com.example.zenith.utils.StringHelper
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AlertsScreen(
    isDay: Boolean = false,
    isArabic: Boolean = false,
    viewModel: AlertViewModel
) {
    val alerts by viewModel.alerts.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var alertToDelete by remember { mutableStateOf<AlertEntity?>(null) }

    WeatherBackground(isDay = isDay) {
        Scaffold(
            floatingActionButton = { AlertFab { showAddSheet = true } },
            containerColor = Color.Transparent
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (alerts.isEmpty()) {
                    AlertsEmptyState(isArabic)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        item {
                            val context = LocalContext.current
                            Text(
                                StringHelper.getString(context, R.string.alerts_title, isArabic),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(alerts, key = { it.id }) { alert ->
                            AlertItem(
                                alert = alert,
                                isArabic = isArabic,
                                onDelete = { alertToDelete = alert },
                                onToggle = { viewModel.toggleAlert(alert) }
                            )
                        }
                    }
                }
            }

            if (alertToDelete != null) {
                com.example.zenith.ui.components.ZenithDeleteDialog(
                    itemName = alertToDelete?.label ?: "",
                    isArabic = isArabic,
                    onDismiss = { alertToDelete = null },
                    onConfirm = { 
                        alertToDelete?.let { viewModel.deleteAlert(it) }
                        alertToDelete = null
                    }
                )
            }

            if (showAddSheet) {
                AddAlertBottomSheet(
                    isArabic = isArabic,
                    onDismiss = { showAddSheet = false },
                    onSave = { 
                        viewModel.addAlert(it)
                        showAddSheet = false
                    }
                )
            }
        }
    }
}
