package com.example.composemediaplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.os.Looper
import com.example.composemediaplayer.data.Song
import com.example.composemediaplayer.shadows.ShadowMediaPlayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowMediaPlayer::class])
class MusicPlayerManagerTest {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private lateinit var context: Context
    private lateinit var mockMediaPlayer: MediaPlayer

    private val fakeSongs = listOf(
        Song(id = 1, title = "Song A", artist = "Artist A", duration = 1000L, filePath = "file://a"),
        Song(id = 2, title = "Song B", artist = "Artist B", duration = 1000L, filePath = "file://a"),
        Song(id = 3, title = "Song C", artist = "Artist C", duration = 1000L, filePath = "file://a"),
        Song(id = 4, title = "Song D", artist = "Artist D", duration = 1000L, filePath = "file://a"),
    )

    @Before
    fun setup() {
        context = mock(Context::class.java)
        mockMediaPlayer = mock()
        musicPlayerManager = spy(MusicPlayerManager(context))
    }

    @After
    fun after() {
        //Release test session
    }

    // =============================
    // Test: play()
    // =============================
    @Test
    fun test_play_shouldSetCurrentSongAndStartPlayback() {
        // given
        assertEquals(-1, musicPlayerManager.currentIndex.value)

        // when
        musicPlayerManager.play(fakeSongs, 1)
        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // then
        assertEquals(1, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[1], musicPlayerManager.currentSong.value)
        assertTrue(musicPlayerManager.isPlaying.value)
    }

    // =============================
    // Test: next()
    // =============================
    @Test
    fun test_next_shouldGoToNextSong() {
        // given
        musicPlayerManager.play(fakeSongs, 0)

        // when
        val nextSong = musicPlayerManager.next()

        // then
        assertEquals(1, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[1], nextSong)
    }

    // =============================
    // Test: next() case end of list
    // =============================
    @Test
    fun test_next_shouldGoToFirstSong() {
        // given
        musicPlayerManager.play(fakeSongs, 3)

        // when
        val nextSong = musicPlayerManager.next()

        // then
        assertEquals(0, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[0], nextSong)
    }

    @Test
    fun test_next_shouldLoopToStartWhenAtEnd() {
        // given
        musicPlayerManager.play(fakeSongs, 2)

        // when
        val nextSong = musicPlayerManager.next()

        // then
        assertEquals(3, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[3], nextSong)
    }

    // =============================
    // Test: prev()
    // =============================
    @Test
    fun test_prev_shouldGoToPreviousSongWhenAtMiddle() {
        // given
        musicPlayerManager.play(fakeSongs, 1)

        // when
        musicPlayerManager.seekTo(0) // reset playback position
        val prevSong = musicPlayerManager.prev()

        // then
        assertEquals(0, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[0], prevSong)
    }

    @Test
    fun test_prev_shouldLoopToEndWhenAtStart() {
        // given
        musicPlayerManager.play(fakeSongs, 0)

        // when
        val prevSong = musicPlayerManager.prev()

        // then
        assertEquals(3, musicPlayerManager.currentIndex.value)
        assertEquals(fakeSongs[3], prevSong)
    }

    // ===========================================================
    // Test getCurrentPosition()
    // ===========================================================
    @Test
    fun test_getCurrentPosition_shouldReturnMediaPlayerPosition() {
        whenever(mockMediaPlayer.currentPosition).thenReturn(12345)
        // inject mock manually
        setPrivateMediaPlayer(mockMediaPlayer)

        val position = musicPlayerManager.getCurrentPosition()
        assertEquals(12345L, position)
    }

    @Test
    fun test_getCurrentPosition_shouldReturnZeroIfMediaPlayerNull() {
        setPrivateMediaPlayer(null)
        val position = musicPlayerManager.getCurrentPosition()
        assertEquals(0L, position)
    }

    // ===========================================================
    // Test updatePlayingState()
    // ===========================================================
    @Test
    fun test_updatePlayingState_shouldChangeIsPlayingFlag() {
        musicPlayerManager.updatePlayingState(true)
        assertTrue(musicPlayerManager.isPlaying.value)

        musicPlayerManager.updatePlayingState(false)
        assertFalse(musicPlayerManager.isPlaying.value)
    }

    // ===========================================================
    // Test onCompletion() should call next()
    // ===========================================================
    @Test
    fun test_onCompletion_shouldCallNext() {
        musicPlayerManager.play(fakeSongs, 0)

        musicPlayerManager.onCompletion(mockMediaPlayer)

        verify(musicPlayerManager).next()
    }

    // ===========================================================
    // Test pause()
    // ===========================================================
    @Test
    fun test_pause() {
        setPrivateMediaPlayer(mockMediaPlayer)
        musicPlayerManager.pause()

        verify(mockMediaPlayer).pause()
    }

    // ===========================================================
    // Test resume()
    // ===========================================================
    @Test
    fun test_resume() {
        setPrivateMediaPlayer(mockMediaPlayer)
        musicPlayerManager.resume()

        verify(mockMediaPlayer).start()
    }

    // ===========================================================
    // Test stop()
    // ===========================================================
    @Test
    fun test_stop() {
        setPrivateMediaPlayer(mockMediaPlayer)
        musicPlayerManager.stop()

        verify(mockMediaPlayer).stop()
        verify(mockMediaPlayer).release()
    }

    // ===========================================================
    // Helpers for accessing private mediaPlayer
    // ===========================================================
    private fun setPrivateMediaPlayer(player: MediaPlayer?) {
        val field = MusicPlayerManager::class.java.getDeclaredField("mediaPlayer")
        field.isAccessible = true
        field.set(musicPlayerManager, player)
    }

    private fun getPrivateMediaPlayer(): MediaPlayer? {
        val field = MusicPlayerManager::class.java.getDeclaredField("mediaPlayer")
        field.isAccessible = true
        return field.get(musicPlayerManager) as? MediaPlayer
    }
}

