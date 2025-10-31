package com.example.composemediaplayer.service

import android.os.Build
import com.example.composemediaplayer.data.Song
import com.example.composemediaplayer.shadows.ShadowPlaybackRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    shadows = [ShadowPlaybackRepository::class]
)
class VehicleDataServiceTest {

    private lateinit var musicService: MusicService

    @Before
    fun setup() {

        val dummySong = Song(
            id = "id",
            title = "title",
            artist = "artist",
            duration = 1L,
            filePath = "fake/path"
        )
        ShadowPlaybackRepository.listSong.add(dummySong)

        musicService = Robolectric.buildService(MusicService::class.java).create().get()
    }

    @After
    fun tearDown() {
        musicService.onDestroy()
        ShadowPlaybackRepository.listSong.clear()
    }

    @Test
    fun test_mediaSessionCallback() {

    }
}
