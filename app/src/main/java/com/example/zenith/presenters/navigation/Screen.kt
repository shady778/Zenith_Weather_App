package com.example.zenith.presenters.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val badgeCount: Int = 0
) {
    object Home : Screen(
        route = "home",
        label = "Home",
        icon = Icons.Rounded.Home,
        selectedIcon = Icons.Rounded.Home
    )

    object Favorites : Screen(
        route = "favorites",
        label = "Favorites",
        icon = Icons.Rounded.FavoriteBorder,
        selectedIcon = Icons.Rounded.Favorite
    )

    object Alerts : Screen(
        route = "alerts",
        label = "Alerts",
        icon = Icons.Rounded.NotificationsNone,
        selectedIcon = Icons.Rounded.Notifications
    )


    object Settings : Screen(
        route = "settings",
        label = "Settings",
        icon = Icons.Rounded.Settings,
        selectedIcon = Icons.Rounded.Settings
    )
}