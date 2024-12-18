package com.example.flexibeat.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.R
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.data.AudioFile
import com.example.flexibeat.ui.viewmodels.LocalStorageModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LocalStorageTab(playerController: PlayerController, scrollToPage: (Int) -> Unit) {
    val context = LocalContext.current
    val localStorageModel = viewModel { LocalStorageModel(context) }
    LaunchedEffect(localStorageModel.relPath) {
        localStorageModel.updateRelPath()
    }
    WithStoragePermission {
        localStorageModel.updateRelPath(localStorageModel.relPath)
        Column(Modifier.fillMaxSize()) {
            Text("Home/${localStorageModel.relPath}", Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge)
            LazyColumn {
                items(localStorageModel.items.folders) { file -> FolderItem(file.name) { localStorageModel.updateRelPath(file) } }
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

@Composable
fun FolderItem(name: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Folder, "Folder", Modifier.size(48.dp), colorScheme.primary)
        Text(name, Modifier.padding(start = 8.dp))
    }
}

@Composable
fun AudioFileItem(audioFile: AudioFile, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (audioFile.coverArt == null)
            Icon(
                painter = painterResource(R.drawable.ic_notification),
                contentDescription = "Cover Art",
                modifier = modifier
                    .size(48.dp)
                    .aspectRatio(1f)
                    .background(colorScheme.inverseOnSurface),
                tint = colorScheme.primary
            )
        else
            Image(audioFile.coverArt.asImageBitmap(), "Cover Art", modifier.size(48.dp))
        Column(modifier = modifier.padding(start = 8.dp)) {
            Text(
                text = audioFile.title?.toString() ?: "Unknown Title",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.artist?.toString() ?: "Unknown Artist",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.album?.toString() ?: "Unknown Album",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        audioFile.duration?.let {
            Spacer(modifier.weight(1f))
            Text(
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it),
                Modifier.align(Alignment.Bottom),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
private fun AudioFileItemPreview() {
    AudioFileItem(AudioFile(0, "Hello World!", "Album Name", "Artist Name", null, 163906L)) {}
}

@Preview(showBackground = true)
@Composable
private fun FolderItemPreview() = FolderItem("Folder Name") {}