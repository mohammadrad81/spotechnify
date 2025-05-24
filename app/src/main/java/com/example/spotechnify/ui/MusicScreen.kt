package com.example.spotechnify.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spotechnify.Musicdata.Musicmodel.Song
import com.example.spotechnify.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Musicviewmodel.User

@Composable
fun SongItem(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            song.image?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Song thumbnail",
                    modifier = Modifier.size(48.dp)
                )
            } ?: Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = song.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = song.artistName, style = MaterialTheme.typography.bodyMedium)
                Text(text = song.genre, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun MusicScreen(navController: NavController,
                viewModel: MusicViewModel,
                user: User,
                onSongItemClick: (List<Song>, Int) -> Unit) {
    viewModel.setUser(user)
    Scaffold {innerPadding->
        val tabs = listOf("All", "For You", "Liked")
        var selectedTab by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
            when (selectedTab) {
                0 -> AllScreen(viewModel, navController, onSongItemClick)
                1 -> ForYouScreen(viewModel, navController, onSongItemClick)
                2 -> LikedScreen(viewModel, navController, onSongItemClick)
            }
        }
    }
}

@Composable
fun AllScreen(viewModel: MusicViewModel,
              navController: NavController,
              onSongItemClick: (List<Song>, Int) -> Unit) {
    val filteredSongs = viewModel.filteredSongs.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search songs, artists, or genres") },
            singleLine = true
        )
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
            filteredSongs.isEmpty() && searchQuery.isNotEmpty() -> {
                Text(
                    text = "No songs found",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            filteredSongs.isEmpty() -> {
                Text(
                    text = "No songs available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn {
//                    items(filteredSongs, key = { it.id }) { song ->
//                        SongItem(song) { navController.navigate("song_details/${song.id}") }
//                    }
                    itemsIndexed(filteredSongs, key = {_, song -> song.id}) {index,  song ->
                        SongItem(song) { onSongItemClick(filteredSongs, index) }
                    }
                }
            }
        }
    }
}

@Composable
fun ForYouScreen(viewModel: MusicViewModel,
                 navController: NavController,
                 onSongItemClick: (List<Song>, Int) -> Unit) {
    val forYouSongs = viewModel.forYouSongs.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    when {
        isLoading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
        forYouSongs.isEmpty() -> {
            Text(
                text = "No recommendations available",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(forYouSongs, key = { it.id }) { song ->
//                    SongItem(song) { navController.navigate("song_details/${song.id}") }
//                }
                itemsIndexed(forYouSongs, key = {_, song -> song.id}) {index,  song ->
                    SongItem(song) { onSongItemClick(forYouSongs, index) }
                }
            }
        }
    }
}

@Composable
fun LikedScreen(viewModel: MusicViewModel,
                navController: NavController,
                onSongItemClick: (List<Song>, Int) -> Unit) {
    val likedSongs = viewModel.likedSongs.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    when {
        isLoading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
        likedSongs.isEmpty() -> {
            Text(
                text = "No liked songs",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(likedSongs, key = { it.id }) { song ->
//                    SongItem(song) { navController.navigate("player_screen?user=${}&") }
//                }
                itemsIndexed(likedSongs, key = {_, song -> song.id}) {index,  song ->
                    SongItem(song) { onSongItemClick(likedSongs, index) }
                }
            }
        }
    }
}

