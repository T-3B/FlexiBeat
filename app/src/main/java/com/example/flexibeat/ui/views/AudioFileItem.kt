package com.example.flexibeat.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flexibeat.data.AudioFile
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AudioFileItem(audioFile: AudioFile, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp).wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        audioFile.coverArt?.let {
            Image(it.asImageBitmap(), "Cover Art", modifier.size(48.dp))
        } ?: FlexiBeatCover(modifier.size(48.dp).aspectRatio(1f).background(colorScheme.inverseOnSurface))

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