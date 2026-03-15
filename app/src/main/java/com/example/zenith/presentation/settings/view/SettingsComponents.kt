package com.example.zenith.presentation.settings.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.ui.theme.ZenithColors
import com.example.zenith.ui.components.GlassCard

enum class TemperatureUnit(val label: String, val symbol: String) {
    CELSIUS("Celsius", "°C"), FAHRENHEIT("Fahrenheit", "°F"), KELVIN("Kelvin", "K")
}

enum class WindUnit(val label: String) { MS("m/s"), MPH("mph") }
enum class AppLanguage(val label: String) { ENGLISH("English"), ARABIC("العربية") }
enum class LocationProvider(val label: String) { GPS("GPS"), MANUAL("Manual") }

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Box(modifier = Modifier.width(3.dp).height(13.dp).clip(RoundedCornerShape(2.dp))
                .background(Brush.verticalGradient(listOf(ZenithColors.Cyan, ZenithColors.Cyan.copy(0.25f)))))
            Text(text = title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = ZenithColors.TextSecondary, letterSpacing = 1.4.sp)
        }
        GlassCard { content() }
    }
}

@Composable
fun <T> SettingsToggleGroup(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    labelOf: (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .background(ZenithColors.Background.copy(alpha = 0.5f))
            .border(1.dp, ZenithColors.BorderGlass, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val bgColor by animateColorAsState(if (isSelected) ZenithColors.Cyan else Color.Transparent)
            val textColor by animateColorAsState(if (isSelected) ZenithColors.Background else ZenithColors.TextSecondary)
            val scale by animateFloatAsState(if (isSelected) 1f else 0.97f)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f).scale(scale).clip(RoundedCornerShape(9.dp))
                    .background(bgColor)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(option) }
                    .padding(vertical = 8.dp)
            ) {
                Text(text = labelOf(option), fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = textColor)
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    subtitle: String,
    showDivider: Boolean = false,
    trailing: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(iconBackground).border(1.dp, iconTint.copy(0.22f), RoundedCornerShape(12.dp))) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 18.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = ZenithColors.TextSecondary,
                    lineHeight = 14.sp
                )
            }
            trailing()
        }
        if (showDivider) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(ZenithColors.BorderGlass.copy(0.1f)))
        }
    }
}