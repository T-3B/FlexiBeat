package com.example.flexibeat.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.flexibeat.aggregators.QueueAggregator
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.data.AudioFile

class QueueTabModel(playerController: PlayerController) : ViewModel() {
    var queue = mutableStateListOf<AudioFile>()
        private set
    var playingQueueIndex by mutableIntStateOf(0)
        private set

    init {
        playerController.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                playingQueueIndex = playerController.getSongIdx
            }

        })
        QueueAggregator.subscribe { audioFiles, idx ->
            queue = mutableStateListOf<AudioFile>().apply { addAll(audioFiles) }
            playingQueueIndex = idx
        }
    }
}