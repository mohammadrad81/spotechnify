package com.example.spotechnify.authentication.components

import androidx.compose.runtime.Composable

@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}