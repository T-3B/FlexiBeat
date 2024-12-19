package com.example.flexibeat.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.ui.viewmodels.QueueTabModel

@Composable
fun QueueTab(playerController: PlayerController, scrollToPage: (Int) -> Unit) {
    val queueTabModel = viewModel { QueueTabModel(playerController) }
    Box(Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(queueTabModel.queue) { idx, audioFile ->
                val modifier = if (idx == queueTabModel.playingQueueIndex) Modifier.background(colorScheme.inverseOnSurface) else Modifier
                AudioFileItem(audioFile, modifier) {
                    playerController.seekToSongIdx(idx)
                    scrollToPage(1)
                }
            }
        }
    }
}