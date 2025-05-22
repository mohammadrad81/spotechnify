package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotechnify.Musicdata.Musicnetwork.NetworkModule
import com.example.spotechnify.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Musicviewmodel.MusicViewModelFactory
import com.example.spotechnify.Musicviewmodel.User
import com.example.spotechnify.ui.MusicScreen
import com.example.spotechnify.ui.theme.SpotechnifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val musicService = NetworkModule.provideMusicService()
        val viewModel = MusicViewModelFactory(musicService).create(MusicViewModel::class.java)
        viewModel.setUser(User("test_id", "test_user", "test@example.com", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzUwNTMwODg5LCJpYXQiOjE3NDc5Mzg4ODksImp0aSI6ImI5NzhjNjMzOGVlZTQyYmE5MzFhZThmNGE0NTZkNGJkIiwidXNlcl9pZCI6NCwiZW1haWxfdmVyaWZpZWQiOmZhbHNlfQ.Az6kUOa-zOR7LIvLFk4cHPytZF8W7taugDK8uRvWJXo"))

        setContent {
            SpotechnifyTheme {
                App(viewModel)
            }
        }
    }
}

@Composable
fun App(viewModel: MusicViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "music_screen") {
        composable("music_screen") {
            MusicScreen(viewModel = viewModel, navController = navController)
        }
    }
}