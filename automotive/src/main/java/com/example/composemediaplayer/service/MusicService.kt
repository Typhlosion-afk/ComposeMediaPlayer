package com.example.composemediaplayer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.composemediaplayer.R
import com.example.composemediaplayer.data.PlaybackRepository
import com.example.composemediaplayer.data.Song
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val CHANNEL_ID = "automotive_music_channel"
private const val NOTIFICATION_ID = 1
private const val BROWSER_ROOT_ID = "automotive_root_id"
private const val TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var mediaPlayerManager: MusicPlayerManager

    @Inject
    lateinit var playbackRepository: PlaybackRepository

    private var songList: List<Song> = emptyList()
    private lateinit var mediaSession: MediaSessionCompat

    private var isServiceStarted = false

    override fun onCreate() {
        super.onCreate()

        songList = playbackRepository.getAllSongs()
        Log.d(TAG, "Fetched ${songList.size} songs.")

        mediaSession = MediaSessionCompat(this, TAG).apply {
            setCallback(mediaSessionCallback)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        createNotificationChannel()
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val songToPlay = songList.find { it.id.toString() == mediaId } ?: return
            val startIndex = songList.indexOf(songToPlay)

            mediaPlayerManager.play(songList, startIndex)
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateMediaMetadata(songToPlay)
            startServiceAndShowNotification(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onPlay() {
            mediaPlayerManager.resume()
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            startServiceAndShowNotification(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onPause() {
            mediaPlayerManager.pause()
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            startServiceAndShowNotification(PlaybackStateCompat.STATE_PAUSED)
        }

        override fun onSkipToNext() {
            mediaPlayerManager.next()
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateMediaMetadata(mediaPlayerManager.currentSong.value)
            startServiceAndShowNotification(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onSkipToPrevious() {
            val prevSong = mediaPlayerManager.prev()
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateMediaMetadata(mediaPlayerManager.currentSong.value)
            startServiceAndShowNotification(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onStop() {
            mediaPlayerManager.stop()
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
            stopForeground(true)
            stopSelf()
        }
    }

    private fun updatePlaybackState(state: Int) {
        val position = mediaPlayerManager.currentIndex.value
        val actions =
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_STOP

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(actions)
            .setState(state, position.toLong(), 1.0f)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun updateMediaMetadata(song: Song?) {
        if (song == null) return
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
            .build()
        mediaSession.setMetadata(metadata)
    }

    @SuppressLint("ForegroundServiceType")
    private fun startServiceAndShowNotification(state: Int) {
        val notification = buildNotification(state).build()

        if (!isServiceStarted) {
            val intent = Intent(this, MusicService::class.java)
            startForegroundService(intent)
            isServiceStarted = true
        }

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(state: Int): NotificationCompat.Builder {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING
        val currentSong = mediaPlayerManager.currentSong.value

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY)
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentSong?.title ?: "No song selected")
            .setContentText(currentSong?.artist ?: "Unknown Artist")
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(createContentIntent())
            .addAction(playPauseAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .setOngoing(isPlaying)
    }

    private fun createContentIntent(): PendingIntent? {
        val openUiIntent = packageManager?.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return if (openUiIntent != null) {
            PendingIntent.getActivity(this, 0, openUiIntent, PendingIntent.FLAG_IMMUTABLE)
        } else null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Channel for Automotive music controls" }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        Log.d(TAG, "onGetRoot: Client connected: $clientPackageName")
        return BrowserRoot(BROWSER_ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (parentId != BROWSER_ROOT_ID) {
            result.sendResult(null)
            return
        }

        val mediaItems = songList.map { song ->
            val description = MediaDescriptionCompat.Builder()
                .setMediaId(song.id.toString())
                .setTitle(song.title)
                .setSubtitle(song.artist)
                .build()
            MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
        }.toMutableList()

        Log.d(TAG, "onLoadChildren: Sending ${mediaItems.size} items to client.")
        result.sendResult(mediaItems)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerManager.stop()
        mediaSession.release()
    }
}