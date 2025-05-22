package com.example.spotechnify.Musicviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotechnify.Musicdata.Musicnetwork.MusicService

class MusicViewModelFactory(private val musicService: MusicService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(musicService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

