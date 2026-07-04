package com.example.coursework


import android.content.Context

object ThemePreference {
    private const val PREFERENCES_FILE = "theme_preferences"
    private const val DARK_MODE_ENABLED = "dark_mode_enabled"

    fun setDarkModeEnabled(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(DARK_MODE_ENABLED, isEnabled)
            apply()
        }
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(DARK_MODE_ENABLED, false)
    }
}