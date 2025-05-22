package com.example.spotechnify.authentication.login

import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AuthTextField(
            value = state.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = "Username",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Password",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.error != null) {
            ErrorText(
                text = state.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthButton(
            text = "Login",
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}