package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//<<<<<<< HEAD
import androidx.activity.enableEdgeToEdge
//<<<<<<< HEAD
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
//=======
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
//>>>>>>> player
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotechnify.Musicdata.Musicnetwork.MusicService

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
fun AuthApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    var musicService: MusicService? = null
    var musicViewModel: MusicViewModel? = null
    var playerViewModel: PlayerViewModel? = null

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
            if (musicService == null || musicViewModel == null){
                musicService = NetworkModule.provideMusicService(user.token)
                musicViewModel = MusicViewModelFactory(musicService).create(MusicViewModel::class.java)
            }
            if(playerViewModel == null){
                playerViewModel= PlayerViewModelFactory(
                    RemoteAudioPlayer(),
                    RemoteLikeService(user.token)
                ).create(PlayerViewModel::class.java)
            }
            MusicScreen(navController, musicViewModel, user) {songslist, index ->
                playerViewModel.loadQueue(songslist, index);
                navController.navigate("player_screen")
            }
        }
        composable("player_screen"){
            MusicPlayerScreen(navController, playerViewModel!!)

        }
    }
}
