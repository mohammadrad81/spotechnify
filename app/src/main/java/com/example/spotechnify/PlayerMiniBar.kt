package com.example.spotechnify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerMiniBar(viewModel: PlayerViewModel, onBarClicked: ()->Unit){
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onBarClicked()
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF121212), Color.Black),
                    startY = 0f,
                    endY = 800f
                )
            )
            .padding(horizontal = 30.dp , vertical = 10.dp)
    ){
        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                bitmap = uiState.trackInformation?.albumArtBitMap?:viewModel.defaultTrackInfo.albumArtBitMap,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Column{
                Text(
                    text = uiState.trackInformation?.name?:"Unknown",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.trackInformation?.artist?:"Unknown",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            IconButton(
                onClick = {viewModel.togglePlayPause()},
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF1DB954), CircleShape)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
