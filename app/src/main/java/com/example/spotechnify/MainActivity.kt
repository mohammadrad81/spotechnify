package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.spotechnify.Musicdata.Musicdatastore.AuthDataStore
import com.example.spotechnify.Musicviewmodel.User
import com.example.spotechnify.Musicdata.Musicnetwork.NetworkModule
import com.example.spotechnify.ui.theme.SpotoTheme
import com.example.spotechnify.view.MusicScreen
import com.example.spotechnify.view.SongDetailsScreen
import com.example.spotechnify.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Musicviewmodel.MusicViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

        lifecycleScope.launch {
            AuthDataStore.saveToken(this@MainActivity, fakeToken)
        }

        val musicService = NetworkModule.provideMusicService(fakeToken)
        val factory = MusicViewModelFactory(musicService)
        val viewModel = ViewModelProvider(this, factory)[MusicViewModel::class.java]

        viewModel.setUser(
            User(
                id = "123",
                username = "testuser",
                email = "test@example.com",
                token = fakeToken
            )
        )

        setContent {
            SpotoTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "music") {
                    composable("music") {
                        MusicScreen(viewModel, navController)
                    }
                    composable("song_details/{songId}") { backStack ->
                        val songId = backStack.arguments?.getString("songId")!!
                        SongDetailsScreen(songId, viewModel, navController)
                    }
                }
            }
        }
    }
}
