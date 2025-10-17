package com.example.composemediaplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.composemediaplayer.core.toTimeString
import com.example.composemediaplayer.data.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaPlayer.OnCompletionListener {

    private var songList: List<Song> = emptyList()
    private var mediaPlayer: MediaPlayer? = null

    private var _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition

    private var trackingJob: Job? = null

    fun startTracking() {
        if (trackingJob?.isActive == true) return

        trackingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val current = mediaPlayer?.currentPosition?.toLong() ?: 0L
                _playbackPosition.value = current
                delay(1000)
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
    }

    fun play(songs: List<Song>, index: Int) {
        songList = songs
        _currentIndex.value = index
        _currentSong.value = songList.getOrNull(currentIndex.value)
        currentSong.value?.let { startPlayback(it) }
    }

    fun next(): Song? {
        if (currentIndex.value < songList.lastIndex) {
            _currentIndex.value++
            _currentSong.value = songList[currentIndex.value]
            startPlayback(songList[currentIndex.value])
        } else {
            _currentIndex.value = 0
            _currentSong.value = songList[currentIndex.value]
            startPlayback(songList[currentIndex.value])
        }
        return currentSong.value
    }

    fun prev(): Song? {
        if(_playbackPosition.value < 5000) {
            // If the user click 2 time in 5 sec, go to previous the song
            if (currentIndex.value > 0) {
                _currentIndex.value--
                _currentSong.value = songList[currentIndex.value]
                startPlayback(songList[currentIndex.value])
            } else {
                _currentIndex.value = songList.lastIndex
                _currentSong.value = songList[currentIndex.value]
                startPlayback(songList[currentIndex.value])
            }
        } else {
            // If the user click 1 time, just replay the current song
            startPlayback(songList[currentIndex.value])
        }
        return currentSong.value
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        _playbackPosition.value = position
    }

    private fun startPlayback(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(song.filePath))
                setOnCompletionListener(this@MusicPlayerManager)
                prepare()
                start()
            }
            seekTo(0)
            _isPlaying.value = true
        } catch (e: Exception) {
            _isPlaying.value = false
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun updatePlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    override fun onCompletion(mp: MediaPlayer?) {
        next()
    }
}
