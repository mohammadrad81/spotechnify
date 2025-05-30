package com.example.spotechnify.Player

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.spotechnify.Music.Musicdata.Musicmodel.Song

class PlayerViewModel (
    private val audioRepository: AudioPlayerRepository,
    private val likeRepository: LikeRepository
) : ViewModel()  {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    private val _isPlayerScreenVisible = MutableStateFlow(false)
    val isPlayerScreenVisible: StateFlow<Boolean> = _isPlayerScreenVisible

    init {
        collectPlaybackStates()
        collectProgressUpdates()
    }

    fun loadQueue(tracks: List<Song>, startIndex: Int = 0) {
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
        if (_uiState.value.trackInformation != null){
            viewModelScope.launch {
                val track = _uiState.value.trackInformation!!
                val res = if (track.liked)
                    likeRepository.unlikeTrack(track.id)
                else
                likeRepository.likeTrack(track.id)

                if (res == LikeServiceResult.Success){
                    _uiState.update { currentState ->
                        val updatedSongs = currentState.trackQueue.map { song ->
                            if (song.id == currentState.trackInformation?.id) song.copy(liked = !song.liked) else song
                        }
                        currentState.copy(trackQueue = updatedSongs)
                    }
                }
            }
        }
    }

    fun setPlayerScreenVisibility(visible: Boolean) {
        _isPlayerScreenVisible.value = visible
    }

    private fun loadAudioFromQueue(info: Song) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                audioRepository.setTrack(info.audioFile) { playNext() }
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
    val trackQueue: List<Song> = emptyList(),
    val trackNumber: Int = 0,
) {
    val trackInformation: Song? =
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

//data class Song(
//    val id: Int,
//    val title: String,
//    val artistName: String,
//    val image: String,
//    val liked: Boolean,
//    val audioFile: String
//)