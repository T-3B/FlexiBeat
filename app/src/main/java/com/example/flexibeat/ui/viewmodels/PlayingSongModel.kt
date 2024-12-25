package com.example.flexibeat.ui.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.example.flexibeat.controllers.PlayerController

val repeatStates = listOf(Icons.Default.Repeat to REPEAT_MODE_OFF, Icons.Default.Repeat to REPEAT_MODE_ALL, Icons.Default.RepeatOne to REPEAT_MODE_ONE)

class PlayingSongModel(playerController: PlayerController) : ViewModel() {
    val loopStateAssociatedIcon get() = repeatStates[loopState].first
    var visualProgress by mutableFloatStateOf(0f)
    var isPlaying by mutableStateOf(false)
        private set
    var loopState by mutableIntStateOf(0)
        private set
    var isShuffled by mutableStateOf(false)
        private set
    var duration by mutableLongStateOf(0L)
        private set
    var title by mutableStateOf("")
        private set
    var artist by mutableStateOf("")
        private set
    var album by mutableStateOf("")
        private set
    var cover: Uri? by mutableStateOf(null)
        private set
    var coverBitmap: Bitmap? by mutableStateOf(null)
        private set

    init {
        refreshMetadata(playerController.mediaMetadata, playerController.currentSong?.albumArtUri?.toUri())

        playerController.addListener(object : Player.Listener {  // add listeners for UI updates
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { duration = playerController.duration }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                if (mediaMetadata != MediaMetadata.EMPTY)
                    refreshMetadata(mediaMetadata, playerController.currentSong?.albumArtUri?.toUri())
            }
            override fun onIsPlayingChanged(isPlayin: Boolean) { isPlaying = isPlayin }
            override fun onPlaybackStateChanged(playbackState: Int) { duration = playerController.duration }
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { visualProgress = playerController.progress }
            override fun onRepeatModeChanged(repeatMode: Int) { loopState = repeatStates.indexOfFirst { it.second == repeatMode } }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) { isShuffled = shuffleModeEnabled }
        })
        val handler = Handler(Looper.getMainLooper())
        lateinit var runnable: Runnable
        runnable = Runnable {
            visualProgress = playerController.progress
            handler.postDelayed(runnable, 1000L)
        }
        handler.postDelayed(runnable, 1000L)
    }

    fun changeLoopState(): Int {
        loopState = (loopState + 1) % repeatStates.size
        return repeatStates[loopState].second
    }

    fun toggleShuffling(): Boolean {
        isShuffled = !isShuffled
        return isShuffled
    }

    fun refreshMetadata(mediaMetadata: MediaMetadata, mediaStoreCover: Uri?) {
        title = mediaMetadata.title?.toString() ?: mediaMetadata.displayTitle?.toString() ?: "Unknown Title"
        artist = mediaMetadata.artist?.toString() ?: "Unknown Artist"
        album = mediaMetadata.albumTitle?.toString() ?: "Unknown Album"
        cover = mediaMetadata.artworkUri ?: mediaStoreCover
        cover ?: { coverBitmap = mediaMetadata.artworkData?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
    }
}