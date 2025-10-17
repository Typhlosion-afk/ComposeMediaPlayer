package com.example.composemediaplayer.core

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
