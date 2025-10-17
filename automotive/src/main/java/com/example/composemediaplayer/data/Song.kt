package com.example.composemediaplayer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: String,
    val title: String = "",
    val artist: String = "",
    val album: String? = null,
    val duration: Long = 0L,
    val artworkUrl: String? = null,
    val filePath: String? = "",
    val isFavorite: Boolean = false,
    val trackNumber: Int? = null,
    val genre: String? = null,
    val releaseYear: Int? = null
) : Parcelable