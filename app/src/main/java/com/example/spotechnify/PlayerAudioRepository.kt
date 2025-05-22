package com.example.spotechnify

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerRepository {
    suspend fun setTrack(url: String)
    fun play()
    fun pause()
    fun getDuration(): Int
    val playbackState: StateFlow<PlaybackState>
    val playbackProgress: Flow<Int>
    fun release()
    fun seekTo(positionMs: Int)
    fun getCurrentPosition(): Int
}

sealed class PlaybackState {
    data object Idle : PlaybackState()
    data object Preparing : PlaybackState()
    data object Ready : PlaybackState()
    data object Playing : PlaybackState()
    data object Paused : PlaybackState()
    data class Error(val message: String) : PlaybackState()
    data object Completed : PlaybackState()
}