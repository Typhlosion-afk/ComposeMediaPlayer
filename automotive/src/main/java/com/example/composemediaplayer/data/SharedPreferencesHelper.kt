package com.example.composemediaplayer.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    fun saveQuery(query: String) {
        val list = getHistory().toMutableList()
        if (!list.contains(query)) {
            list.add(0, query)
        }
        prefs.edit { putStringSet("history_list", list.toSet()) }
    }

    fun getHistory(): List<String> {
        return prefs.getStringSet("history_list", emptySet())?.toList() ?: emptyList()
    }

    fun clearHistory() {
        prefs.edit { putStringSet("history_list", emptySet()) }
    }


}