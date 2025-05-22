package com.example.spotechnify.authentication.signup

import androidx.compose.runtime.Composable

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.isSignUpSuccessful) {
        if (state.isSignUpSuccessful) {
            onSignUpSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sign Up",
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
            value = state.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Email",
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
            text = "Sign Up",
            onClick = { viewModel.signUp() },
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Login")
        }
    }
}