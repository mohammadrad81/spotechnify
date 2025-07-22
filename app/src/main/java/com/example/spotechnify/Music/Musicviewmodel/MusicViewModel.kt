package com.example.spotechnify.Music.Musicviewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotechnify.Music.Musicdata.Musicmodel.Song
import com.example.spotechnify.Music.Musicdata.Musicnetwork.MusicService
import com.example.spotechnify.Music.Musicviewmodel.Util.downloadFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

class MusicViewModel(private val musicService: MusicService) : ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user = _user

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs.asStateFlow()

    private val _forYouSongs = MutableStateFlow<List<Song>>(emptyList())
    val forYouSongs: StateFlow<List<Song>> = _forYouSongs.asStateFlow()

    private val _likedSongs = MutableStateFlow<List<Song>>(emptyList())
    val likedSongs: StateFlow<List<Song>> = _likedSongs.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredSongs = MutableStateFlow<List<Song>>(emptyList())
    val filteredSongs: StateFlow<List<Song>> = _filteredSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun logWithTime(tag: String, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        Log.d(tag, "[$timestamp] $message")
    }

    init {
        logWithTime("MusicViewModel", "ViewModel initialized, fetching songs...")
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allSongs = musicService.getAllSongs()
                Log.d("MusicViewModel", "Fetched ${allSongs.size} songs successfully.")
                _allSongs.value = allSongs
                _filteredSongs.value = allSongs

                _user.value?.let {
                    val recommendedSongs = musicService.getRecommendedSongs()
                    val likedSongsList = musicService.getLikedSongs()
                    Log.d("MusicViewModel", "Recommended songs: ${recommendedSongs.size}, Liked songs: ${likedSongsList.size}")
                    _forYouSongs.value = recommendedSongs
                    _likedSongs.value = likedSongsList
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error fetching songs: ${e.message}", e)
                _filteredSongs.value = emptyList()
                _forYouSongs.value = emptyList()
                _likedSongs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                logWithTime("MusicViewModel", "Search query updated: \"$query\"")
                _isLoading.value = true
                _filteredSongs.value = if (query.isEmpty()) {
                    musicService.getAllSongs().also {
                        logWithTime("MusicViewModel", "Search query empty, fetched all songs (${it.size})")
                    }
                } else {
                    musicService.searchSongs(query).also {
                        logWithTime("MusicViewModel", "Search results for \"$query\": ${it.size} songs")
                    }
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error searching query \"$query\": ${e.localizedMessage}", e)
                _filteredSongs.value = emptyList()
            } finally {
                _isLoading.value = false
                logWithTime("MusicViewModel", "Search operation finished")
            }
        }
    }

    fun setUser(user: User) {
        logWithTime("MusicViewModel", "Setting user: ${user.username}")
        _user.value = user
        fetchSongs()
    }

    fun downloadSongFile(context: Context, song: Song) {
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
        downloadFile(context, song.audioFile, "${song.title} - ${song.artistName}.mp3")
    }
}

@Serializable
data class User(val id: String, val username: String, val email: String, val token: String)
