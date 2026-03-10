package com.example.zenith.presenters.alerts.view
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.airbnb.lottie.compose.*
import com.example.zenith.R
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.data.datasource.local.database.AlertEntity
import java.util.UUID

@Composable
fun AlertFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ZenithColors.Cyan,
        shape = CircleShape
    ) {
        Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertBottomSheet(isArabic: Boolean, onDismiss: () -> Unit, onSave: (AlertEntity) -> Unit) {
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        selectedHour = timePickerState.hour
        selectedMinute = timePickerState.minute
    }
    var selectedType by remember { mutableStateOf(AlertType.ALARM) }
    var selectedTrigger by remember { mutableStateOf(WeatherTrigger.ANY) }
    var selectedRepeat by remember { mutableStateOf(RepeatMode.EVERY_DAY) }
    var thresholdValue by remember { mutableStateOf(25f) }
    var alertLabel by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(0.2f)) }
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                if (isArabic) "تخصيص التنبيه" else "Customize Alert",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                if (isArabic) "اختر متى تريد استقبال إشعارات الطقس" else "Choose when to be notified about the weather",
                fontSize = 14.sp,
                color = Color.White.copy(0.5f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            SheetSectionLabel(if (isArabic) "اختر الوقت" else "Select Time", Icons.Rounded.Schedule)
            
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color.White.copy(0.05f),
                        clockDialSelectedContentColor = Color.Black,
                        clockDialUnselectedContentColor = Color.White,
                        selectorColor = ZenithColors.Cyan,
                        periodSelectorBorderColor = ZenithColors.Cyan,
                        periodSelectorSelectedContainerColor = ZenithColors.Cyan,
                        periodSelectorSelectedContentColor = Color.Black,
                        periodSelectorUnselectedContentColor = Color.White,
                        timeSelectorSelectedContainerColor = ZenithColors.Cyan.copy(0.2f),
                        timeSelectorSelectedContentColor = ZenithColors.Cyan,
                        timeSelectorUnselectedContainerColor = Color.White.copy(0.05f),
                        timeSelectorUnselectedContentColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SheetSectionLabel(if (isArabic) "محفز الطقس" else "Weather Trigger", Icons.Rounded.FilterList)
            TriggerSelector(selected = selectedTrigger, isArabic = isArabic, onSelect = { 
                selectedTrigger = it
                thresholdValue = when(it) {
                    WeatherTrigger.TEMPERATURE -> 25f
                    WeatherTrigger.WIND_SPEED -> 10f
                    WeatherTrigger.RAIN, WeatherTrigger.CLOUDS -> 50f
                    else -> 0f
                }
            })

            if (selectedTrigger == WeatherTrigger.TEMPERATURE || 
                selectedTrigger == WeatherTrigger.WIND_SPEED || 
                selectedTrigger == WeatherTrigger.RAIN ||
                selectedTrigger == WeatherTrigger.CLOUDS) {
                
                Spacer(modifier = Modifier.height(24.dp))
                val range = when(selectedTrigger) {
                    WeatherTrigger.TEMPERATURE -> -20f..50f
                    WeatherTrigger.WIND_SPEED -> 0f..100f
                    WeatherTrigger.RAIN, WeatherTrigger.CLOUDS -> 0f..100f
                    else -> 0f..100f
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SheetSectionLabel(if (isArabic) "القيمة المحددة" else "Threshold Value", Icons.Rounded.Tune)
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${thresholdValue.toInt()}${selectedTrigger.unit}",
                        color = selectedTrigger.color,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = thresholdValue,
                    onValueChange = { thresholdValue = it },
                    valueRange = range,
                    colors = SliderDefaults.colors(
                        thumbColor = selectedTrigger.color,
                        activeTrackColor = selectedTrigger.color,
                        inactiveTrackColor = Color.White.copy(0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SheetSectionLabel(if (isArabic) "نوع التنبيه" else "Alert Mode", Icons.Rounded.Settings)
            AlertTypeToggle(selected = selectedType, isArabic = isArabic, onSelect = { selectedType = it })

            Spacer(modifier = Modifier.height(24.dp))

            SheetSectionLabel(if (isArabic) "التكرار" else "Frequency", Icons.Rounded.Repeat)
            RepeatSelector(selected = selectedRepeat, isArabic = isArabic, onSelect = { selectedRepeat = it })

            Spacer(modifier = Modifier.height(24.dp))

            SheetSectionLabel(if (isArabic) "عنوان التنبيه" else "Alert Label", Icons.Rounded.Label)
            OutlinedTextField(
                value = alertLabel,
                onValueChange = { alertLabel = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isArabic) "مثلاً: مظلة المطر" else "e.g., Rain Umbrella", color = Color.White.copy(0.3f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = ZenithColors.Cyan,
                    unfocusedBorderColor = Color.White.copy(0.1f),
                    cursorColor = ZenithColors.Cyan
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    onSave(
                        AlertEntity(
                            id = UUID.randomUUID().toString(),
                            hour = selectedHour,
                            minute = selectedMinute,
                            type = selectedType,
                            trigger = selectedTrigger,
                            triggerValue = if (selectedTrigger in listOf(WeatherTrigger.TEMPERATURE, WeatherTrigger.WIND_SPEED, WeatherTrigger.RAIN, WeatherTrigger.CLOUDS)) thresholdValue.toInt() else null,
                            repeat = selectedRepeat,
                            isEnabled = true,
                            label = alertLabel.ifBlank { if (isArabic) "تنبيه الطقس" else "Weather Update" }
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZenithColors.Cyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (isArabic) "تفعيل التنبيه" else "Enable Alert", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TriggerSelector(selected: WeatherTrigger, isArabic: Boolean, onSelect: (WeatherTrigger) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(WeatherTrigger.entries.toTypedArray()) { trigger ->
            val isSelected = selected == trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) trigger.color.copy(0.15f) else Color.White.copy(0.05f))
                    .border(1.dp, if (isSelected) trigger.color else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { onSelect(trigger) }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = trigger.icon,
                    contentDescription = null,
                    tint = if (isSelected) trigger.color else Color.White.copy(0.4f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    trigger.getLabel(isArabic),
                    color = if (isSelected) Color.White else Color.White.copy(0.4f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AlertItem(alert: AlertEntity, isArabic: Boolean, onDelete: () -> Unit, onToggle: (AlertEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(CircleShape).background(alert.trigger.color.copy(0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(alert.trigger.icon, null, tint = alert.trigger.color, modifier = Modifier.size(28.dp))
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(ZenithColors.Cyan)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color(0xFF1E293B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = alert.type.icon,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.label,
                    color = Color.White.copy(0.5f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = alert.getFormattedTime(isArabic),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(alert.type.icon, null, tint = ZenithColors.Cyan, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        alert.type.getLabel(isArabic),
                        color = ZenithColors.Cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(" • ", color = Color.White.copy(0.2f))
                    Text(
                        alert.trigger.getLabel(isArabic) + (if (alert.triggerValue != null) " (${alert.triggerValue}${alert.trigger.unit})" else ""),
                        color = alert.trigger.color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Rounded.Repeat,
                        contentDescription = null,
                        tint = Color.White.copy(0.3f),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = alert.repeat.getLabel(isArabic),
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Switch(
                checked = alert.isEnabled,
                onCheckedChange = { onToggle(alert) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ZenithColors.Cyan,
                    checkedTrackColor = ZenithColors.Cyan.copy(0.3f),
                    uncheckedTrackColor = Color.White.copy(0.1f)
                )
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = Color.Red.copy(0.5f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TimeScroller(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) }) {
            Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null, tint = ZenithColors.Cyan)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) }) {
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = ZenithColors.Cyan)
        }
    }
}

@Composable
fun RepeatSelector(selected: RepeatMode, isArabic: Boolean, onSelect: (RepeatMode) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RepeatMode.entries.forEach { mode ->
            val isSelected = selected == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color.White.copy(0.15f) else Color.White.copy(0.05f))
                    .border(
                        1.dp,
                        if (isSelected) ZenithColors.Cyan.copy(0.5f) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { onSelect(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.getLabel(isArabic),
                    color = if (isSelected) ZenithColors.Cyan else Color.White.copy(0.6f),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SheetSectionLabel(label: String, icon: ImageVector? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        if (icon != null) {
            Icon(icon, null, tint = Color.White.copy(0.4f), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(label.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.4f))
    }
}

@Composable
fun AlertTypeToggle(selected: AlertType, isArabic: Boolean, onSelect: (AlertType) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)).padding(4.dp)) {
        AlertType.entries.forEach { type ->
            val isSelected = selected == type
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ZenithColors.Cyan else Color.Transparent)
                    // تم التعديل هنا: استخدام الـ clickable البسيط لحل المشكلة
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { onSelect(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(type.getLabel(isArabic), color = if (isSelected) Color.Black else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlertsEmptyState(isArabic: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.weather_night_rain_dark))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (isArabic) "لا توجد تنبيهات" else "No alerts set",
            color = Color.White.copy(0.6f),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            if (isArabic) "اضغط على + لإضافة تنبيهات طقس جديدة" else "Tap + to wake up your weather alerts",
            color = Color.White.copy(0.3f)
        )
    }
}