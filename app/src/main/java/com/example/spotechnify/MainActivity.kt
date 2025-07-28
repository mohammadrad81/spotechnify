package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotechnify.Authentication.AuthViewModel
import com.example.spotechnify.Authentication.LoginScreen
import com.example.spotechnify.Authentication.SignUpScreen
import com.example.spotechnify.Authentication.WelcomeScreen
import androidx.navigation.navArgument
import com.example.spotechnify.Music.Musicdata.Musicnetwork.NetworkModule
import com.example.spotechnify.Music.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Music.Musicviewmodel.MusicViewModelFactory
import com.example.spotechnify.Music.Musicviewmodel.User
import com.example.spotechnify.Music.ui.MusicScreen
import com.google.gson.Gson
import com.example.spotechnify.Music.Musicdata.Musicnetwork.MusicService
import com.example.spotechnify.Player.MusicPlayerScreen
import com.example.spotechnify.Player.PlayerViewModel
import com.example.spotechnify.Player.PlayerViewModelFactory
import com.example.spotechnify.Player.RemoteAudioPlayer
import com.example.spotechnify.Player.RemoteLikeService
import com.example.spotechnify.ui.theme.MyAppTheme
import com.example.spotechnify.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { ThemeViewModel(application) }
                }
            )
            val isDarkTheme by themeViewModel.isDarkMode.collectAsState()

            MyAppTheme(isDarkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthApp(themeViewModel)
                }
            }
        }
    }
}

@Composable
fun AuthApp(themeViewModel: ThemeViewModel) {
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
            WelcomeScreen(navController,
                themeViewModel.isDarkMode.collectAsState().value,
                toggleDarkMode = { themeViewModel.toggleTheme()})
        }
        composable("login") {
            LoginScreen(navController, authViewModel,
                themeViewModel.isDarkMode.collectAsState().value,
                toggleDarkMode = { themeViewModel.toggleTheme()})
        }
        composable("signup") {
            SignUpScreen(navController, authViewModel,
                themeViewModel.isDarkMode.collectAsState().value,
                toggleDarkMode = { themeViewModel.toggleTheme()})
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
                musicViewModel = MusicViewModelFactory(musicService!!).create(MusicViewModel::class.java)
           }
            if(playerViewModel == null){
                playerViewModel= PlayerViewModelFactory(
                    RemoteAudioPlayer(),
                    RemoteLikeService(user.token)
                ).create(PlayerViewModel::class.java)
            }

            val context = LocalContext.current
            MusicScreen(
                navController = navController,
                viewModel = musicViewModel,
                user = user,
                onDownloadClicked = { song -> musicViewModel!!.downloadSongFile(context,song)},
                onSongItemClick = { songslist, index ->
                    playerViewModel.loadQueue(songslist, index)
                    navController.navigate("player_screen")
                },
                isDark = themeViewModel.isDarkMode.collectAsState().value,
                toggleDarkMode = { themeViewModel.toggleTheme() }
            )
        }
        composable("player_screen"){
            MusicPlayerScreen(navController, playerViewModel!!)

        }
    }
}
