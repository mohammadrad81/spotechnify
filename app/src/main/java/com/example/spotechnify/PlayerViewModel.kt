package com.example.spotechnify

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.graphics.Canvas
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerViewModel (private val audioRepository: AudioPlayerRepository) : ViewModel()  {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    private val _isPlayerScreenVisible = MutableStateFlow(false)
    val isPlayerScreenVisible: StateFlow<Boolean> = _isPlayerScreenVisible

    val defaultBitMap = ImageBitmap(100, 100).apply {
        val canvas = Canvas(this.asAndroidBitmap())
        canvas.drawColor(android.graphics.Color.WHITE)
    }

    init {
        collectPlaybackStates()
        collectProgressUpdates()
//        loadQueue(defaultTrackQueue)
    }

    fun loadQueue(tracks: List<TrackInformation>, startIndex: Int = 0) {
        resetTrack()
        _uiState.update {
            it.copy(
                trackNumber = startIndex,
                trackQueue = tracks
            )
        }
        loadAudioFromQueue(tracks[startIndex])
    }

    fun togglePlayPause() {
        when (audioRepository.playbackState.value) {
            is PlaybackState.Playing -> audioRepository.pause()
            is PlaybackState.Ready, is PlaybackState.Paused -> audioRepository.play()
            else -> Unit
        }
    }

    fun playNext() {
        audioRepository.release()
        if (_uiState.value.trackInformation != null && _uiState.value.trackQueue.isNotEmpty()) {
            val newIndex = (_uiState.value.trackNumber + 1) % _uiState.value.trackQueue.size
            val newTrack = _uiState.value.trackQueue[newIndex]
            resetTrack()
            _uiState.update {
                it.copy(trackNumber = newIndex)
            }
            loadAudioFromQueue(newTrack)
            Log.e("CONTROLLERS", "NEW INDEX IS ${_uiState.value.trackNumber}")
        }
    }

    fun playPrevious() {
        audioRepository.release()
        if (_uiState.value.trackInformation != null) {
            val newIndex = if (_uiState.value.trackNumber > 0) {
                _uiState.value.trackNumber - 1
            } else {
                _uiState.value.trackQueue.lastIndex
            }
            val newTrack = _uiState.value.trackQueue[newIndex]
            resetTrack()
            _uiState.update {
                it.copy(trackNumber = newIndex)
            }
            loadAudioFromQueue(newTrack)
        }
    }

    fun seekTo(position: Float) {
        audioRepository.seekTo((position * audioRepository.getDuration()).toInt())
    }

    fun toggleLike(){
        // TODO: an api call must be done
        if (_uiState.value.trackInformation != null){
            _uiState.update { currentState ->
                val updatedSongs = currentState.trackQueue.map { song ->
                    if (song.id == currentState.trackInformation?.id) song.copy(isLiked = !song.isLiked) else song
                }
                currentState.copy(trackQueue = updatedSongs)
            }
        }
    }

    fun setPlayerScreenVisibility(visible: Boolean) {
        _isPlayerScreenVisible.value = visible
    }

    private fun loadAudioFromQueue(info: TrackInformation) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                audioRepository.setTrack(info.url) { playNext() }
            }
        }
    }

    private fun collectPlaybackStates() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                audioRepository.playbackState.collect { state ->
                    _uiState.update {
                        it.copy(
                            playbackState = state,
                            isLoading = state != PlaybackState.Playing && state != PlaybackState.Paused && state != PlaybackState.Idle,
                            error = (state as? PlaybackState.Error)?.message,
                            durationMs = audioRepository.getDuration(),
                        )
                    }
                }
            }
        }
    }

    private fun collectProgressUpdates() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                audioRepository.playbackProgress.collect { progress ->
                    _uiState.update { it.copy(progressMs = progress) }
                }
            }
        }
    }

    override fun onCleared() {
        audioRepository.release()
        super.onCleared()
    }

    private fun resetTrack(){
        _uiState.update {
            it.copy(
                playbackState = PlaybackState.Idle,
                progressMs = 0,
                durationMs = 0,
                error = null,
                trackNumber = 0
            )
        }
    }

}

data class PlayerUiState(
    val playbackState: PlaybackState = PlaybackState.Idle,
    val progressMs: Int = 0,
    val durationMs: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val trackQueue: List<TrackInformation> = emptyList(),
    val trackNumber: Int = 0,
) {
    val trackInformation: TrackInformation? =
        if(trackQueue.isNotEmpty() && trackNumber < trackQueue.size)
            trackQueue[trackNumber] else null
    val isPlaying: Boolean get() = playbackState == PlaybackState.Playing
    val formattedDuration: String get() = formatTime(durationMs)
    val formattedPosition: String get() = formatTime(progressMs)
    val progress: Float get() = getProgress(progressMs, durationMs)

    @SuppressLint("DefaultLocale")
    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getProgress(pr: Int, du: Int): Float {
        if(du == 0){
            return 0F
        }
        return pr.toFloat() / du
    }
}

data class TrackInformation(
    val id: Int,
    val title: String,
    val artist: String,
    val albumArtBitMap: ImageBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).asImageBitmap(),
    val isLiked: Boolean,
    val url: String
)