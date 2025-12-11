package com.example.composemediaplayer.ui.childview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.composemediaplayer.core.AppDestinations

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object SongList : BottomNavItem(AppDestinations.SONG_LIST_ROUTE, "Songs", Icons.Default.List)
    object Speedometer : BottomNavItem(AppDestinations.SPEEDOMETER_ROUTE, "Speed", Icons.Default.Speed)
    object Setting : BottomNavItem(AppDestinations.SETTINGS_ROUTE, "Settings", Icons.Default.Settings)
    object Navigation: BottomNavItem(AppDestinations.NAVIGATION_ROUTE, "Navigation", Icons.Default.Navigation)
}
