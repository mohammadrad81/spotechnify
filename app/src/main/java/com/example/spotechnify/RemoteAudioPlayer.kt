package com.example.spotechnify

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteAudioPlayer (
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): AudioPlayerRepository {

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    private val _playbackProgress = MutableStateFlow(0)

    private var mediaPlayer: MediaPlayer? = null
    private var progressUpdateJob: Job? = null

    override val playbackState: StateFlow<PlaybackState> = _playbackState
    override val playbackProgress: Flow<Int> = _playbackProgress

    private var errorMessage by mutableStateOf<String?>(null)

    init {
        _playbackState.value = PlaybackState.Idle
    }

    override suspend fun setTrack(url: String) = withContext(Dispatchers.IO) {
        try {
            _playbackState.value = PlaybackState.Preparing
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener { mp ->
//                    audioDuration.floatValue = mp.duration.toFloat()
                    _playbackState.value = PlaybackState.Ready

                    if (isPlaying) {
                        mp.start()
                        startProgressUpdates()
                    }
                }

                setOnCompletionListener {
                    _playbackState.value = PlaybackState.Completed
                    stopProgressUpdates()
                }

                setOnErrorListener { _, what, extra ->
                    val error = "MediaPlayer error: what=$what, extra=$extra"
                    _playbackState.value = PlaybackState.Error(error)
                    stopProgressUpdates()
                    false
                }

                reset()
                setDataSource(url)
                prepareAsync()
            }
        } catch (e: Exception) {
            errorMessage = "Error initializing player: ${e.message}"
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = CoroutineScope(dispatcher).launch {
            while (_playbackState.value == PlaybackState.Playing) {
                mediaPlayer?.let { mp ->
                    if (mp.duration > 0) {
                        _playbackProgress.value = mp.currentPosition
                        Log.d("ProgressUpdates","progress updated with the value ${mp.currentPosition}")
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    override fun play() {
        if (_playbackState.value == PlaybackState.Ready ||
            _playbackState.value == PlaybackState.Paused) {
            mediaPlayer?.start()
            _playbackState.value = PlaybackState.Playing
            startProgressUpdates()
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _playbackState.value = PlaybackState.Paused
        stopProgressUpdates()
    }

    override fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playbackProgress.value = if (getDuration() > 0) positionMs else 0
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        _playbackState.value = PlaybackState.Idle
    }

    override fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}