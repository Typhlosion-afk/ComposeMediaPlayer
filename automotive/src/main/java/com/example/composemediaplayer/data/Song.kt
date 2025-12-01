package com.example.composemediaplayer.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    val title: String,
    val artist: String,
    val album: String? = null,
    val filePath: String,
    val duration: Long,
    val albumArtUri: String? = null,
    val isFavorite: Boolean = false
) : Parcelable