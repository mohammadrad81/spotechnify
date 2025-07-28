package com.example.spotechnify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotechnify.datastore.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            ThemePreference.getThemeFlow(application).collect {
                _isDarkMode.value = it
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            ThemePreference.saveTheme(getApplication(), !_isDarkMode.value)
        }
    }
}