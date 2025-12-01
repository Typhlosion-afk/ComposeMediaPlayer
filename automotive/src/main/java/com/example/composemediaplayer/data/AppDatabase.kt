package com.example.composemediaplayer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}