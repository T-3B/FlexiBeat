package com.example.flexibeat.ui.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.example.flexibeat.controllers.PlayerController

class PlayingSongModel(playerController: PlayerController) : ViewModel() {
    var visualProgress by mutableFloatStateOf(0f)
    var isPlaying by mutableStateOf(false)
    var duration by mutableLongStateOf(0L)
    var title by mutableStateOf("")
    var artist by mutableStateOf("")
    var album by mutableStateOf("")
    var cover: Uri? by mutableStateOf(null)
    var coverBitmap: Bitmap? by mutableStateOf(null)

    init {
        refreshMetadata(playerController.mediaMetadata)

        playerController.addListener(object : Player.Listener {  // add listeners for UI updates
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { duration = playerController.duration }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                if (mediaMetadata != MediaMetadata.EMPTY)
                    refreshMetadata(mediaMetadata)
            }
            override fun onIsPlayingChanged(isPlayin: Boolean) { isPlaying = isPlayin }
            override fun onPlaybackStateChanged(playbackState: Int) { duration = playerController.duration }
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { visualProgress = playerController.progress }
        })
        val handler = Handler(Looper.getMainLooper())
        lateinit var runnable: Runnable
        runnable = Runnable {
            visualProgress = playerController.progress
            handler.postDelayed(runnable, 1000L)
        }
        handler.postDelayed(runnable, 1000L)
    }

    fun refreshMetadata(mediaMetadata: MediaMetadata) {
        title = mediaMetadata.title?.toString() ?: mediaMetadata.displayTitle?.toString() ?: "Unknown Title"
        artist = mediaMetadata.artist?.toString() ?: "Unknown Artist"
        album = mediaMetadata.albumTitle?.toString() ?: "Unknown Album"
        cover = mediaMetadata.artworkUri
        coverBitmap = mediaMetadata.artworkData?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }
}