package com.example.spotechnify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotechnify.Musicdata.Musicnetwork.MusicService
import com.example.spotechnify.Musicviewmodel.MusicViewModel

class PlayerViewModelFactory(
    private val audioRepository: AudioPlayerRepository,
    private val likeRepository: LikeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(audioRepository, likeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}