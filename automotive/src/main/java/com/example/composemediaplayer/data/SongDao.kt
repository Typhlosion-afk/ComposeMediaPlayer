package com.example.composemediaplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    // Get all songs and observe changes with a Flow
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    // Update an existing song (we will use this to toggle the favorite state)
    @Update
    suspend fun updateSong(song: Song)

    // Although not used yet, this would be useful for a "Favorites" screen
    @Query("SELECT * FROM songs WHERE isFavorite = 1")
    fun getFavoriteSongs(): Flow<List<Song>>

    // Add this method to insert a list of songs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<Song>)
}