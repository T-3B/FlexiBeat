package com.example.flexibeat.ui.views

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import coil3.compose.AsyncImage
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.ui.theme.UserColors
import com.example.flexibeat.ui.viewmodels.PlayingSongModel
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import java.text.SimpleDateFormat
import java.util.Locale

val repeatStates = listOf(Icons.Default.Repeat to REPEAT_MODE_OFF, Icons.Default.Repeat to REPEAT_MODE_ALL, Icons.Default.RepeatOne to REPEAT_MODE_ONE)

@Composable
fun PlayingSongTab(playerController: PlayerController) {
    Log.d("blabla", UserColors.primary.toArgb().toLong().toString(16))
    val playingSongModel = viewModel { PlayingSongModel(playerController) }
    if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE)
        Row(Modifier.fillMaxSize().padding(16.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            CoverArt(playingSongModel, Modifier.weight(1f))
            Column(Modifier.weight(1f).padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                SongMetadata(playingSongModel)
                CurrentSongButtons(playerController)
                MusicSeekBar(playingSongModel, playerController)
                PlaybackControls(playingSongModel, playerController)
            }
        }
    else
        Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Bottom, Alignment.CenterHorizontally) {
            CoverArt(playingSongModel, Modifier.weight(1f))
            SongMetadata(playingSongModel)
            CurrentSongButtons(playerController)
            MusicSeekBar(playingSongModel, playerController)
            PlaybackControls(playingSongModel, playerController)
        }
}


@Composable
fun CoverArt(playingSongModel: PlayingSongModel, modifier: Modifier) {
    Box(modifier.fillMaxSize()) {
        val modifierSub = Modifier.align(Alignment.Center).aspectRatio(1f).background(colorScheme.inverseOnSurface)

        playingSongModel.coverBitmap?.let { Image(it.asImageBitmap(), "Album art", modifierSub) }
            ?: playingSongModel.cover?.let { AsyncImage(it, "Album art", modifierSub) }
            ?: FlexiBeatCover(modifierSub)
    }
}

@Composable
fun SongMetadata(playingSongModel: PlayingSongModel) {
    Column(Modifier.padding(bottom = 16.dp), Arrangement.spacedBy(7.dp), Alignment.CenterHorizontally) {
        Text(playingSongModel.title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(playingSongModel.artist, fontSize = 24.sp)
        Text(playingSongModel.album, fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun CurrentSongButtons(playerController: PlayerController) {
    Row(Modifier.fillMaxWidth(), Arrangement.Start) {
        var loopState by remember { mutableIntStateOf(0) }
        IconButton(onClick = {
            loopState = (loopState + 1) % 3
            playerController.repeatMode = repeatStates[loopState].second
        }) {
            Icon(repeatStates[loopState].first, "Loop (or not) queue or current song", tint = LocalContentColor.current.let { if (loopState == 0) it.copy(0.5f) else it })
        }
        var isShuffling by remember { mutableStateOf(false) }
        IconButton(onClick = {
            isShuffling = !isShuffling
            playerController.shuffleModeEnabled = isShuffling
        }) {
            Icon(Icons.Default.Shuffle, "Shuffle queue or not", tint = LocalContentColor.current.let { if (isShuffling) it else it.copy(.5f) })
        }
    }
}

@Composable
fun MusicSeekBar(playingSongModel: PlayingSongModel, playerController: PlayerController) {
    Row(Modifier.fillMaxWidth()) {
        Text(SimpleDateFormat("mm:ss", Locale.getDefault()).format(playingSongModel.visualProgress * playingSongModel.duration),
            Modifier.align(Alignment.CenterVertically).padding(6.dp))
        Seeker(
            value = playingSongModel.visualProgress,
            onValueChange = { playingSongModel.visualProgress = it },
            onValueChangeFinished = { playerController.seekTo((playingSongModel.visualProgress * playingSongModel.duration).toLong()) },
            segments = playerController.chapters,
            dimensions = SeekerDefaults.seekerDimensions(gap = 4.dp),
            colors = SeekerDefaults.seekerColors(progressColor = colorScheme.primary, thumbColor = colorScheme.primary),
            modifier = Modifier.weight(1f)
        )
        Text(SimpleDateFormat("mm:ss", Locale.getDefault()).format(playingSongModel.duration),
            Modifier.align(Alignment.CenterVertically).padding(6.dp))
    }
}

@Composable
fun PlaybackControls(playingSongModel: PlayingSongModel, playerController: PlayerController) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
        IconButton(onClick = playerController::seekPrevSong, Modifier.padding(0.dp)) {
            Icon(Icons.Filled.SkipPrevious, "Previous Track")
        }
        Icon(
            Icons.Filled.FastRewind,
            contentDescription = "Fast Rewind",
            modifier = Modifier
                .indication(
                    remember { MutableInteractionSource() },
                    LocalIndication.current
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) {}
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { playerController.seekBackward() },
                        onLongPress = { playerController.seekPrevChapter() }
                    )
                }
        )
        IconButton(onClick = playerController::playPause) {
            Icon(if (playingSongModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause")
        }
        Icon(Icons.Filled.FastForward, "Fast Forward", Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { playerController.seekForward() },
                onLongPress = { playerController.seekNextChapter() })
        })

        IconButton(onClick = playerController::seekNextSong) {
            Icon(Icons.Filled.SkipNext, "Next Track")
        }
    }
}


@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Preview(showBackground = true)
@Composable
private fun PlayingSongTabPreview() {
    PlayingSongTab(PlayerController(LocalContext.current))
}