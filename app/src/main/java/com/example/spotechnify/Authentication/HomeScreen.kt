package com.example.spotechnify.Authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, viewModel: AuthViewModel) {
    val authResult = viewModel.authResult.collectAsState().value
    val user = when (authResult) {
        is AuthViewModel.AuthResult.Success -> (authResult as AuthViewModel.AuthResult.Success).user
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text(
                text = "Welcome, ${user.firstName} ${user.lastName}!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Username: ${user.username}")
            Text(text = "Email: ${user.email}")
        }

        Button(
            onClick = {
                viewModel.resetAuthResult()
                navController.navigate("welcome") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .padding(top = 32.dp)
        ) {
            Text("Logout")
        }
    }
}