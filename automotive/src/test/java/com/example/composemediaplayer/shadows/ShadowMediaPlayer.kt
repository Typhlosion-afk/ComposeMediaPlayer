package com.example.composemediaplayer.shadows

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.io.IOException

@Implements(MediaPlayer::class)
class ShadowMediaPlayer {
    @Implementation
    @Throws(
        IOException::class,
        IllegalArgumentException::class,
        SecurityException::class,
        IllegalStateException::class
    )
    fun setDataSource(context: Context, uri: Uri) {
    }
}