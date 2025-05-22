package com.example.spotechnify.authentication

import androidx.compose.runtime.Composable

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to App",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AuthButton(
            text = "Login",
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Sign Up",
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth(),
            isSecondary = true
        )
    }
}