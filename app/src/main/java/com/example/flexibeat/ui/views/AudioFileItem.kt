package com.example.flexibeat.ui.views

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flexibeat.data.AudioFile
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AudioFileItem(audioFile: AudioFile, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp).wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val bitmap = audioFile.coverPath?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                try {
                    context.contentResolver.loadThumbnail(Uri.parse(it), Size(48, 48), null)
                } catch (e: IOException) {
                    null
                }
            else
                BitmapFactory.decodeFile(it)
        }
        bitmap?.let { Image(it.asImageBitmap(), "Cover art", modifier.size(48.dp)) }
            ?: FlexiBeatCover(modifier.size(48.dp).aspectRatio(1f).background(colorScheme.inverseOnSurface))

        Column(modifier = modifier.padding(start = 8.dp)) {
            Text(
                text = audioFile.title ?: "Unknown Title",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.album ?: "Unknown Album",
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