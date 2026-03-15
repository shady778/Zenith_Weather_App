package com.example.zenith.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenith.ui.components.GlassCard
import com.example.zenith.ui.theme.ZenithColors

@Composable
fun ZenithBottomNav(
    currentRoute: String?,
    isDay: Boolean,
    isArabic: Boolean = false,
    onSelect: (Screen) -> Unit,
) {
    val items = remember {
        listOf(
            Screen.Home,
            Screen.Favorites,
            Screen.Alerts,
            Screen.Settings
        )
    }

    val navBgColor by animateColorAsState(
        targetValue = if (isDay) Color(0xFF1E88E5) else ZenithColors.Background,
        animationSpec = tween(1500),
        label = "nav_bg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(navBgColor)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = ZenithColors.Cyan.copy(alpha = 0.2f)
                ),
            cornerRadius = 26.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    
                    NavItem(
                        screen = screen,
                        selected = isSelected,
                        isDay = isDay,
                        isArabic = isArabic,
                        onClick = { onSelect(screen) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NavItem(
    screen: Screen,
    selected: Boolean,
    isDay: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val animatedIconAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.6f,
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            this@Column.AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + scaleIn(initialScale = 0.5f),
                exit = fadeOut() + scaleOut(targetScale = 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ZenithColors.Cyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }

                Icon(
                    imageVector = if (selected) screen.selectedIcon else screen.icon,
                    contentDescription = screen.label,
                    tint = if (selected) {
                        if (isDay) Color.White else ZenithColors.Cyan
                    } else {
                        if (isDay) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.7f)
                    },
                    modifier = Modifier
                        .size(if (selected) 26.dp else 24.dp)
                        .scale(animatedScale)
                )
        }
        Text(
            text = when(screen) {
                Screen.Home -> if (isArabic) "الرئيسية" else "Home"
                Screen.Favorites -> if (isArabic) "المفضلة" else "Favorites"
                Screen.Alerts -> if (isArabic) "التنبيهات" else "Alerts"
                Screen.Settings -> if (isArabic) "الإعدادات" else "Settings"
            },
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                if (isDay) Color.White else ZenithColors.Cyan
            } else {
                if (isDay) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f)
            },
            modifier = Modifier.alpha(animatedIconAlpha)
        )

        // Indicator Dot
        Spacer(Modifier.height(4.dp))
        this@Column.AnimatedVisibility(
            visible = selected,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                if (isDay) Color.White else ZenithColors.Cyan,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
