package com.example.composemediaplayer.shadows

import com.example.composemediaplayer.data.PlaybackRepository
import com.example.composemediaplayer.data.Song
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(PlaybackRepository::class)
class ShadowPlaybackRepository {
    companion object {
        var listSong = ArrayList<Song>()
    }
    @Implementation
    fun getAllSongs(): List<Song> {
        return listSong
    }
}