package com.example.zenith.presenters.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.presenters.favorites.viewmodel.*
import com.example.zenith.ui.components.GlassCard
import com.example.zenith.ui.theme.ZenithColors

@Composable
fun CityCard(city: FavoriteCity, showCelsius: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    val temp = if (showCelsius) "${city.tempC.toInt()}°C" else "${(city.tempC * 9 / 5).toInt() + 32}°F"

    val interactionSource = remember { MutableInteractionSource() }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        backgroundColor = city.condition.gradientStart.copy(alpha = 0.06f)
            .compositeOver(ZenithColors.SurfaceGlass)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            city.condition.tint.copy(alpha = 0.8f),
                            city.condition.tint.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(city.condition.tint.copy(alpha = 0.12f))
                    .border(1.dp, city.condition.tint.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = city.condition.icon,
                    contentDescription = null,
                    tint = city.condition.tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenithColors.TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = city.description,
                        fontSize = 11.sp,
                        color = city.condition.tint.copy(alpha = 0.85f)
                    )
                    Text("·", color = ZenithColors.TextSecondary.copy(alpha = 0.5f))
                    Text(
                        text = city.localTime,
                        fontSize = 11.sp,
                        color = ZenithColors.TextSecondary
                    )
                }
            }
            Text(
                text = temp,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ZenithColors.TextPrimary
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    null,
                    tint = ZenithColors.TextDisabled,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}