package com.example.composemediaplayer.ui.screen.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
        const val KEY_USE_MPH = "use_mph_enabled"
    }

    fun saveDarkModeState(isEnabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_DARK_MODE, isEnabled) }
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveUseMphState(isEnabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_USE_MPH, isEnabled) }
    }

    fun isMphEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_USE_MPH, false)
    }
}
