package com.example.composemediaplayer.core

import android.annotation.SuppressLint

object AppDestinations {
    const val SPEEDOMETER_ROUTE = "speedometer"
    const val SETTINGS_ROUTE = "settings"
    const val NOW_PLAYING_ROUTE = "nowPlaying"
    const val NAVIGATION_ROUTE = "navigation"
    const val SONG_LIST_ROUTE = "song_list"
}

@SuppressLint("DefaultLocale")
fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
