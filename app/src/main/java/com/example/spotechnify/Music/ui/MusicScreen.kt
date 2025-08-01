package com.example.spotechnify.Music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spotechnify.Music.Musicdata.Musicmodel.Song
import com.example.spotechnify.Music.Musicviewmodel.MusicViewModel
import com.example.spotechnify.Music.Musicviewmodel.User
import com.example.spotechnify.R

// import for share button
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun SongItem(song: Song, onDownloadClicked: (Song)->Unit, onClick: () -> Unit) {
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {onDownloadClicked(song)},
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_file_download_24),
                    contentDescription = "Download",
                    modifier = Modifier.size(20.dp)
                    )
            }
        }
    }
}

@Composable
fun MusicScreen(navController: NavController,
                viewModel: MusicViewModel,
                user: User,
                onDownloadClicked: (Song) -> Unit,
                onSongItemClick: (List<Song>, Int) -> Unit,
                isDark: Boolean,
                toggleDarkMode: () -> Unit) {
    viewModel.setUser(user)
    Scaffold {innerPadding->
        val tabs = listOf("All", "For You", "Liked")
        var selectedTab by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDark) "Dark Mode: ON" else "Dark Mode: OFF",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(checked = isDark, onCheckedChange = { toggleDarkMode() })
            }
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
                0 -> AllScreen(viewModel, navController, onDownloadClicked, onSongItemClick)
                1 -> ForYouScreen(viewModel, navController, onDownloadClicked, onSongItemClick)
                2 -> LikedScreen(viewModel, navController, onDownloadClicked, onSongItemClick)
            }
        }
    }
}

@Composable
fun AllScreen(viewModel: MusicViewModel,
              navController: NavController,
              onDownloadClicked: (Song) -> Unit,
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
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
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
                    color = MaterialTheme.colorScheme.outline,
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
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn {
                    itemsIndexed(filteredSongs, key = {_, song -> song.id}) {index,  song ->
                        SongItem(song,{onDownloadClicked(song)}) { onSongItemClick(filteredSongs, index) }

                    }
                }
            }
        }
    }
}


@Composable
fun ForYouScreen(viewModel: MusicViewModel,
                 navController: NavController,
                 onDownloadClicked: (Song) -> Unit,
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
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(forYouSongs, key = {_, song -> song.id}) {index,  song ->
                    SongItem(song,onDownloadClicked) { onSongItemClick(forYouSongs, index) }
                }
            }
        }
    }
}

@Composable
fun LikedScreen(viewModel: MusicViewModel,
                navController: NavController,
                onDownloadClicked: (Song) -> Unit,
                onSongItemClick: (List<Song>, Int) -> Unit) {
    val likedSongs = viewModel.likedSongs.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // share button
        Button(
            onClick = {
                // create share text
                val shareText = likedSongs.joinToString(separator = "\n\n") { song ->
                    // format
                    "${song.title} - ${song.artistName}\nlink:\n${song.audioFile}"
                }

                // copy to clipboard
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Liked Songs", shareText)
                clipboard.setPrimaryClip(clip)


                Toast.makeText(context, "Liked songs copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            enabled = likedSongs.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Share")
        }

        // no change
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
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
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(likedSongs, key = { _, song -> song.id }) { index, song ->
                        SongItem(song, onDownloadClicked) { onSongItemClick(likedSongs, index) }
                    }
                }
            }
        }
    }
}

