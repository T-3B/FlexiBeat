package com.example.flexibeat.controllers

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.flexibeat.data.AudioFile
import dev.vivvvek.seeker.Segment

class PlayerController(context: Context) {
    var queue by mutableStateOf(listOf<AudioFile>())
        private set
    var chapters = mutableStateListOf(Segment(name = "Intro", start = 0f), Segment(name = "Part 1", start = .33f), Segment(name = "Part 2", start = .67f))
    private val player = ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
        .build().also {
            it.addListener(object: Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { it.play() }
                override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { it.play() }
            })
        }
    val duration get() = player.duration
    val getSongIdx get() = player.currentMediaItemIndex
    val mediaMetadata get() = player.mediaMetadata
    val progress get() = player.currentPosition.toFloat() / player.duration
    var repeatMode get() = player.repeatMode
        set(value) { player.repeatMode = value }
    var shuffleModeEnabled get() = player.shuffleModeEnabled
        set(value) { player.shuffleModeEnabled = value }

    fun addListener(listener: Player.Listener) { player.addListener(listener) }
    fun destroyPlayer() = player.release()
    fun seekTo(pos: Long) { player.seekTo(pos) }
    fun seekBackward() { player.seekBack() }
    fun seekForward() { player.seekForward() }
    fun seekNextChapter() { seekTo(((chapters.find { it.start > progress + 1e-6f }?.start ?: 1f) * duration).toLong()) }
    fun seekPrevChapter() { chapters.findLast { it.start < progress - 1e-3f }?.let { player.seekTo((it.start * duration).toLong()) } }
    fun seekNextSong() { player.seekToNextMediaItem() }
    fun seekPrevSong() { player.seekToPreviousMediaItem() }
    fun seekToSongIdx(idx: Int) { player.seekTo(idx, C.TIME_UNSET) }
    fun replaceQueue(audioFiles: List<AudioFile>, initialIdx: Int) {
        queue = audioFiles
        player.apply {
            setMediaItems(audioFiles.map { MediaItem.fromUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, it.id)) }, initialIdx, C.TIME_UNSET)
            prepare()
            play()
        }
    }
    fun playPause() { player.playWhenReady = !player.isPlaying }
}