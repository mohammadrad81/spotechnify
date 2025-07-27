package com.example.spotechnify.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit

private val Context.dataStore by preferencesDataStore("settings")

object ThemePreference {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun getThemeFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[DARK_MODE_KEY] ?: false
        }
    }

    suspend fun saveTheme(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDarkMode
        }
    }
}
