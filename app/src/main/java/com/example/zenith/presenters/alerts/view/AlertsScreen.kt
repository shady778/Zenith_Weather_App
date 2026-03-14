package com.example.zenith.presenters.alerts.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.R
import com.example.zenith.data.datasource.local.database.AlertEntity
import com.example.zenith.presenters.alerts.viewmodel.AlertEvent
import com.example.zenith.presenters.alerts.viewmodel.AlertViewModel
import com.example.zenith.presenters.home.ui.WeatherBackground
import com.example.zenith.utils.StringHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ToastData(val message: String, val isSuccess: Boolean)


@Composable
private fun ZenithSnackbar(data: ToastData) {
    val iconTint  = if (data.isSuccess) Color(0xFF4CAF82) else Color(0xFFEF5350)
    val iconBg    = if (data.isSuccess)
        Brush.radialGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)))
    else
        Brush.radialGradient(listOf(Color(0xFF7F0000), Color(0xFFB71C1C)))
    val icon: ImageVector = if (data.isSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Delete

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A1A2E).copy(alpha = 0.95f), Color(0xFF16213E).copy(alpha = 0.95f))
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = data.message,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .width(3.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(50))
                .background(iconTint)
        )
    }
}

@Composable
fun AlertsScreen(
    isDay: Boolean = false,
    isArabic: Boolean = false,
    viewModel: AlertViewModel
) {
    val alerts by viewModel.alerts.collectAsState()
    var showAddSheet  by remember { mutableStateOf(false) }
    var alertToDelete by remember { mutableStateOf<AlertEntity?>(null) }

    var currentToast  by remember { mutableStateOf<ToastData?>(null) }
    var showToast     by remember { mutableStateOf(false) }

    val scope   = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            val msg = when (event) {
                is AlertEvent.Added   -> StringHelper.getString(context, R.string.alert_added_success,   isArabic)
                is AlertEvent.Deleted -> StringHelper.getString(context, R.string.alert_deleted_success, isArabic)
            }
            currentToast = ToastData(msg, isSuccess = event is AlertEvent.Added)
            showToast    = true
            scope.launch {
                delay(2800)
                showToast = false
            }
        }
    }

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
                                alert    = alert,
                                isArabic = isArabic,
                                onDelete = { alertToDelete = alert },
                                onToggle = { viewModel.toggleAlert(alert) }
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible  = showToast,
                    enter    = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit     = slideOutVertically(targetOffsetY  = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    currentToast?.let { ZenithSnackbar(it) }
                }
            }

            if (alertToDelete != null) {
                com.example.zenith.ui.components.ZenithDeleteDialog(
                    itemName  = alertToDelete?.label ?: "",
                    isArabic  = isArabic,
                    onDismiss = { alertToDelete = null },
                    onConfirm = {
                        alertToDelete?.let { viewModel.deleteAlert(it) }
                        alertToDelete = null
                    }
                )
            }

            if (showAddSheet) {
                AddAlertBottomSheet(
                    isArabic  = isArabic,
                    onDismiss = { showAddSheet = false },
                    onSave    = {
                        viewModel.addAlert(it)
                        showAddSheet = false
                    }
                )
            }
        }
    }
}

