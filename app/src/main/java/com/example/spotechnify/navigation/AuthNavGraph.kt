package com.example.spotechnify.navigation

import androidx.compose.runtime.Composable

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.Welcome.route,
    onAuthComplete: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(NavRoutes.Login.route) },
                onSignUpClick = { navController.navigate(NavRoutes.SignUp.route) }
            )
        }

        composable(NavRoutes.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = onAuthComplete,
                onNavigateToSignUp = { navController.navigate(NavRoutes.SignUp.route) }
            )
        }

        composable(NavRoutes.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = onAuthComplete,
                onNavigateToLogin = { navController.navigate(NavRoutes.Login.route) }
            )
        }
    }
}