package com.example.spotechnify.Musicviewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotechnify.Musicdata.Musicmodel.Song
import com.example.spotechnify.Musicdata.Musicnetwork.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allSongs = musicService.getAllSongs()
                _allSongs.value = allSongs
                _filteredSongs.value = allSongs

                _user.value?.let {
                    _forYouSongs.value = musicService.getRecommendedSongs()
                    _likedSongs.value = musicService.getLikedSongs()
                }
            } catch (e: Exception) {
                _filteredSongs.value = emptyList()
                _forYouSongs.value = emptyList()
                _likedSongs.value = empty_ist()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _filteredSongs.value = if (query.isEmpty()) {
                    musicService.getAllSongs()
                } else {
                    musicService.searchSongs(query)
                }
            } catch (e: Exception) {
                _filteredSongs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setUser(user: User) {
        _user.value = user
        fetchSongs()
    }
}

data class User(val id: String, val username: String, val email: String, val token: String)

