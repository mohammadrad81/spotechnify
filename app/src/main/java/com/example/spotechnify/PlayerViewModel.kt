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

class PlayerViewModel (private val audioRepository: AudioPlayerRepository) : ViewModel()  {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    private val _isPlayerScreenVisible = MutableStateFlow(false)
    val isPlayerScreenVisible: StateFlow<Boolean> = _isPlayerScreenVisible

    val defaultTrackInfo = TrackInformation(
        name = "song 1",
        artist = "SoundHelix",
        albumArtBitMap = ImageBitmap(100, 100).apply {
            val canvas = Canvas(this.asAndroidBitmap())
            canvas.drawColor(android.graphics.Color.WHITE)
        },
        isLiked = false,
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    )

    init {
        collectPlaybackStates()
        collectProgressUpdates()
        loadAudio(defaultTrackInfo)
    }

    fun loadAudio(info: TrackInformation) {
        reset()
        _uiState.update {
            it.copy(
                trackInformation = info
            )
        }
        viewModelScope.launch {
            audioRepository.setTrack(info.url)
        }

    }

    fun togglePlayPause() {
        when (audioRepository.playbackState.value) {
            is PlaybackState.Playing -> audioRepository.pause()
            is PlaybackState.Ready, is PlaybackState.Paused -> audioRepository.play()
            else -> {
                Log.d("PLAY BUTTON","YOOOOOOOKH")
                Unit
            }
        }
    }

    fun seekTo(position: Float) {
        audioRepository.seekTo((position * audioRepository.getDuration()).toInt())
    }

    fun toggleLike(){
        // TODO: an api call must be done
        if (_uiState.value.trackInformation != null){
            _uiState.update {
                it.copy(
                    trackInformation = it.trackInformation?.copy(
                        isLiked = !it.trackInformation.isLiked
                    )
                )
            }
        }
    }

    private fun collectPlaybackStates() {
        viewModelScope.launch {
            audioRepository.playbackState.collect { state ->
                _uiState.update {
                    Log.d("sdasd", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa")
                    it.copy(
                        playbackState = state,
                        isLoading = state == PlaybackState.Preparing,
                        error = (state as? PlaybackState.Error)?.message,
                        durationMs = audioRepository.getDuration(),
                    )
                }
            }
        }
    }

    private fun collectProgressUpdates() {
        viewModelScope.launch {
            audioRepository.playbackProgress.collect { progress ->
                _uiState.update { it.copy(progressMs = progress) }
            }
        }
    }

    override fun onCleared() {
        audioRepository.release()
        super.onCleared()
    }

    fun setPlayerScreenVisibility(visible: Boolean) {
        _isPlayerScreenVisible.value = visible
    }

    fun reset(){
        _uiState.update {
            it.copy(
                playbackState = PlaybackState.Idle,
                progressMs = 0,
                durationMs = 0,
                isLoading = false,
                error = null,
                trackInformation = null
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
    val trackInformation: TrackInformation? = null
) {
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
    val name: String,
    val artist: String,
    val albumArtBitMap: ImageBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).asImageBitmap(),
    val isLiked: Boolean,
    val url: String
)