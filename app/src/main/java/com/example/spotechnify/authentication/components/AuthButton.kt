package com.example.spotechnify.authentication.components

import androidx.compose.runtime.Composable

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSecondary) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(text)
        }
    }
}