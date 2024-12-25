package com.example.flexibeat.ui.views

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.OptIn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import coil3.Bitmap
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import com.example.flexibeat.data.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
fun AudioFileItem(audioFile: AudioFile, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp).wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumArt(audioFile, modifier)

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

@Composable
private fun AlbumArt(audioFile: AudioFile, modifier: Modifier) {
    val context = LocalContext.current
    var albumArtUri by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(audioFile) {
        albumArtUri = withContext(Dispatchers.IO) {
            audioFile.albumArtUri?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        context.contentResolver.loadThumbnail(it.toUri(), Size(48, 48), null)
                    } catch (e: IOException) {
                        null
                    }
                } else {
                    BitmapFactory.decodeFile(it)
                }
            } ?: run {
                MediaMetadataRetriever().apply {
                    setDataSource(
                        context,
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            audioFile.id
                        )
                    )
                }.embeddedPicture?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            }
        }
    }


    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context).data(albumArtUri).crossfade(true).size(coil3.size.Size(48, 48)).precision(Precision.INEXACT).build(),
        contentDescription = "Cover art",
        modifier = modifier.size(48.dp).aspectRatio(1f),
        error = { FlexiBeatCover(modifier.size(48.dp).aspectRatio(1f).background(colorScheme.inverseOnSurface)) }
    )
}

@Preview(showBackground = true)
@Composable
private fun AudioFileItemPreview() {
    AudioFileItem(AudioFile(0, "Hello World!", "Album Name", "Artist Name", 163906L, "")) {}
}