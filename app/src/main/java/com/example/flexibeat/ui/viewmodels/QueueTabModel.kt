package com.example.flexibeat.ui.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.flexibeat.controllers.PlayerController

class QueueTabModel(playerController: PlayerController) : ViewModel() {
    val queue by derivedStateOf { playerController.queue }
    var playingQueueIndex by mutableIntStateOf(0)
        private set

    init {
        playerController.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                playingQueueIndex = playerController.getSongIdx
            }
        })
    }
}