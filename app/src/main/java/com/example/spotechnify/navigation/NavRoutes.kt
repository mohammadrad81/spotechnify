package com.example.spotechnify.navigation

sealed class NavRoutes(val route: String) {
    object Welcome : NavRoutes("welcome")
    object Login : NavRoutes("login")
    object SignUp : NavRoutes("signup")
    object Home : NavRoutes("home") // Your main screen after auth
}