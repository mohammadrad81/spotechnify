package com.example.spotechnify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun MusicPlayerScreen(navController: NavHostController, viewModel: PlayerViewModel){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setPlayerScreenVisibility(true)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setPlayerScreenVisibility(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF121212), Color.Black),
                    startY = 0f,
                    endY = 800f
                )
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown, // Replace with your icon
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            AsyncImage(
                model = uiState.trackInformation?.coverImageUrl,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = uiState.trackInformation?.title?:"Unknown",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.trackInformation?.artist?:"Unknown",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick = { viewModel.toggleLike() }) {
                    Icon(
                        imageVector = if (uiState.trackInformation?.isLiked == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (uiState.trackInformation?.isLiked == true) Color(0xFF1DB954) else Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = uiState.progress,
                    onValueChange = { newValue -> viewModel.seekTo(newValue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color(0xFF1DB954),
                        inactiveTrackColor = Color.Gray
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.formattedPosition,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = uiState.formattedDuration,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
// TODO: play on shuffle
//                IconButton(onClick = { isShuffleOn = !isShuffleOn }) {
//                    Icon(
//                        imageVector = Icons.Default.Shuffle,
//                        contentDescription = "Shuffle",
//                        tint = if (isShuffleOn) Color(0xFF1DB954) else Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }

                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = {viewModel.togglePlayPause()},
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF1DB954), CircleShape)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
// TODO: repeat
//                IconButton(onClick = { isRepeatOn = !isRepeatOn }) {
//                    Icon(
//                        imageVector = Icons.Default.Repeat,
//                        contentDescription = "Repeat",
//                        tint = if (isRepeatOn) Color(0xFF1DB954) else Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}