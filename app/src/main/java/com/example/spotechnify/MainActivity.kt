package com.example.spotechnify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.spotechnify.ui.theme.SpotechnifyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val defaultTrackQueue = listOf(
    TrackInformation(
        id = 1,
        title = "song 1",
        artist = "SoundHelix",
        isLiked = false,
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        coverImageUrl = "https://static-cse.canva.com/blob/2031574/1600w-cV8ArcnU-oA.jpg",
    ),
    TrackInformation(
        id = 2,
        title = "song 2",
        artist = "SoundHelix",
        isLiked = false,
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        coverImageUrl = "https://static-cse.canva.com/blob/2031574/1600w-cV8ArcnU-oA.jpg",
    ),
    TrackInformation(
        id = 3,
        title = "song 3",
        artist = "SoundHelix",
        isLiked = false,
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        coverImageUrl = "https://static-cse.canva.com/blob/2031574/1600w-cV8ArcnU-oA.jpg",
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playerViewModel = PlayerViewModel(RemoteAudioPlayer(), RemoteLikeService(""))
        playerViewModel.loadQueue(defaultTrackQueue)

        enableEdgeToEdge()
        setContent {
            SpotechnifyTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val isVisible by playerViewModel.isPlayerScreenVisible.collectAsState()
                        if (!isVisible) {
                            PlayerMiniBar(viewModel = playerViewModel, {navController.navigate("player")})
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home", // or your actual start destination
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        ) }
                        composable("player") { MusicPlayerScreen(navController, playerViewModel) }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpotechnifyTheme {
        Greeting("Android")
    }
}
