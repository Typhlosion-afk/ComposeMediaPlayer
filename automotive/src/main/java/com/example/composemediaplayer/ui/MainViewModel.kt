package com.example.composemediaplayer.ui

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemediaplayer.data.PlaybackRepository
import com.example.composemediaplayer.data.Song
import com.example.composemediaplayer.service.MusicPlayerManager
import com.example.composemediaplayer.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.composemediaplayer.data.SongDao
import com.example.composemediaplayer.ui.screen.setting.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PlaybackRepository,
    private val mediaPlayerManager: MusicPlayerManager,
    private val settingsManager: SettingsManager,
    private val songDao: SongDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _currentListSong = MutableStateFlow<List<Song>>(emptyList())
    val currentListSong: StateFlow<List<Song>> = songDao.getAllSongs()
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val updatedSong = song.copy(isFavorite = !song.isFavorite)
            songDao.updateSong(updatedSong)
        }
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshSongs() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadData()
            Log.d("TAG", "refreshSongs: Reload")
            delay(1000)
            _isRefreshing.value = false
        }
    }

    private lateinit var mediaBrowser: MediaBrowserCompat
    private var mediaController: MediaControllerCompat? = null

    val currentIndex = mediaPlayerManager.currentIndex
    val currentSong = mediaPlayerManager.currentSong
    val isPlaying = mediaPlayerManager.isPlaying
    val playbackPosition = mediaPlayerManager.playbackPosition

    // --- Dark Mode State ---
    private val _isDarkModeEnabled = MutableStateFlow(settingsManager.isDarkModeEnabled())
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    // --- MPH State ---
    private val _isMphEnabled = MutableStateFlow(settingsManager.isMphEnabled())
    val isMphEnabled: StateFlow<Boolean> = _isMphEnabled.asStateFlow()

    fun setDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.saveDarkModeState(isEnabled)
            _isDarkModeEnabled.update { isEnabled }
        }
    }

    fun setUseMph(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.saveUseMphState(isEnabled)
            _isMphEnabled.update { isEnabled }
        }
    }

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.d("MainViewModel", "MediaBrowser connected")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(controllerCallback)
            }
        }

        override fun onConnectionSuspended() {
            Log.d("MainViewModel", "MediaBrowser connection suspended")
            mediaController?.unregisterCallback(controllerCallback)
            mediaController = null
        }

        override fun onConnectionFailed() {
            Log.d("MainViewModel", "MediaBrowser connection failed")
        }
    }

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            mediaPlayerManager.updatePlayingState(state?.state == PlaybackStateCompat.STATE_PLAYING)
        }
    }

    init {
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MusicService::class.java),
            connectionCallback,
            null
        ).apply { connect() }
        mediaPlayerManager.startTracking()
    }


    fun loadData() {
        viewModelScope.launch {
            val songs = withContext(Dispatchers.IO) {
                repository.getAllSongs()
            }
            _currentListSong.value = songs
            songDao.insertAll(_currentListSong.value)
        }
    }

    fun playSong(song: Song) {
        mediaController?.transportControls?.playFromMediaId(song.id.toString(), null)
    }

    fun pause() {
        mediaController?.transportControls?.pause()
    }

    fun resume() {
        mediaController?.transportControls?.play()
    }

    fun next() {
        mediaController?.transportControls?.skipToNext()
    }

    fun previous() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        mediaPlayerManager.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        // Disconnect from the MediaBrowser when the ViewModel is destroyed
        mediaController?.unregisterCallback(controllerCallback)
        mediaPlayerManager.stopTracking()
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }
}
