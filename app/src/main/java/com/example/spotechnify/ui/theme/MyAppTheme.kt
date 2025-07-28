package com.example.spotechnify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.spotechnify.Music.ui.theme.MusicDarkColors
import com.example.spotechnify.Music.ui.theme.MusicLightColors
import com.example.spotechnify.Music.ui.theme.Typography

@Composable
fun MyAppTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) MusicDarkColors else MusicLightColors,
        typography = Typography,
        content = content
    )
}