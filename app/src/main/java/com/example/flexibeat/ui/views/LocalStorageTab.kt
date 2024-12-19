package com.example.flexibeat.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.ui.viewmodels.LocalStorageModel

@Composable
fun LocalStorageTab(playerController: PlayerController, scrollToPage: (Int) -> Unit) {
    WithStoragePermission {
        val context = LocalContext.current
        val localStorageModel = viewModel { LocalStorageModel(context) }
        Column(Modifier.fillMaxSize()) {
            Text("Home/${localStorageModel.relPath}", Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge)
            LazyColumn {
                items(localStorageModel.items.folders) { folder -> FolderItem(folder.name) { localStorageModel.relPath = folder } }
                itemsIndexed(localStorageModel.items.files) { idx, audioFile ->
                    AudioFileItem(audioFile) {
                        playerController.replaceQueue(localStorageModel.items.files, idx)
                        scrollToPage(1)
                    }
                }
            }
        }
    }
}