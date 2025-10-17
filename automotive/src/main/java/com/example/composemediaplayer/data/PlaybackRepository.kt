package com.example.composemediaplayer.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.composemediaplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getAllSongs(): List<Song> {
        val rawSongs = listOf(
            Triple(R.raw.take_a_breath, "Take a breath", "Artist 1"),
            Triple(R.raw.go_to_the_field, "Go to the field", "Artist 2"),
            Triple(R.raw.radio_lo_fi_ncs, "Radio lo-fi ncs", "Artist 3"),
            Triple(R.raw.laid__back_night, "Laid back night", "Artist 3"),
            Triple(R.raw.midnight_reverie, "Midnight reverie", "Artist 3"),
            Triple(R.raw.radio_lo_fi_beats, "Radio lo-fi beats", "Artist 3"),
            Triple(R.raw.smoke_weed_everyday, "Smoke weed everyday", "Artist 3"),
            Triple(R.raw.wade_in_the_water, "Wake in the water", "Artist 3"),
        )

        return rawSongs.mapIndexed { index, (resId, title, artist) ->
            val afd = context.resources.openRawResourceFd(resId)

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            val duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()

            Song(
                id = resId.toString(),
                title = title,
                artist = artist,
                duration = duration,
                filePath = "android.resource://${context.packageName}/raw/${context.resources.getResourceEntryName(resId)}"
            )
        }
    }

    fun getAllSongs1(): List<Song> {
        val songList = mutableListOf<Song>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL)
        } else {
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        }

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Audio.Media.RELATIVE_PATH)
            }
        }.toTypedArray()

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                Log.d("PlaybackRepository", "Cursor count: ${cursor.count}")
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val uri = Uri.withAppendedPath(collection, id.toString())

                    songList.add(
                        Song(
                            id = id.toString(),
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            filePath = uri.toString()
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("PlaybackRepository", "Failed to query MediaStore. Missing permission?", e)
        }


        Log.d("PlaybackRepository", "getAllSongs: Found ${songList.size} songs.")

        return songList
    }
}
