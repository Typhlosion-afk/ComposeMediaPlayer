package com.example.composemediaplayer.ui.childview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object SongList : BottomNavItem("song_list", "Songs", Icons.Default.List)
    object Speedometer : BottomNavItem("speedometer", "Speed", Icons.Default.Speed)
}
