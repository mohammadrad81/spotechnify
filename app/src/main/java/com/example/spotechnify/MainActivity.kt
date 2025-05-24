package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//<<<<<<< HEAD
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotechnify.Authentication.AuthViewModel
import com.example.spotechnify.Authentication.HomeScreen
import com.example.spotechnify.Authentication.LoginScreen
import com.example.spotechnify.Authentication.SignUpScreen
import com.example.spotechnify.Authentication.WelcomeScreen
//=======
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spotechnify.Musicdata.Musicnetwork.NetworkModule
import com.example.spotechnify.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Musicviewmodel.MusicViewModelFactory
import com.example.spotechnify.Musicviewmodel.User
import com.example.spotechnify.ui.MusicScreen
import com.example.spotechnify.ui.theme.SpotechnifyTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AuthApp()
            }
        }
    }
}

@Composable
//<<<<<<< HEAD
fun AuthApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val musicService = NetworkModule.provideMusicService()
    val musicViewModel = MusicViewModelFactory(musicService).create(MusicViewModel::class.java)

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("signup") {
            SignUpScreen(navController, authViewModel)
        }
        composable("home") {
            HomeScreen(navController, authViewModel)
        }
        composable("music_screen?user={user}",
            arguments = listOf(
                navArgument("user"){
                    type = NavType.StringType
                    nullable = false
                    defaultValue = ""
                }
            )){
            val userJson = it.arguments?.getString("user")
            val user = Gson().fromJson(userJson, User::class.java)
            MusicScreen(navController, musicViewModel, user)
        }
    }
}