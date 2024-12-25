package com.example.flexibeat.controllers

import android.content.ContentUris
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.flexibeat.data.AudioFile
import com.example.flexibeat.data.datasave.GlobalRepository
import com.example.flexibeat.data.datasave.QueueDatabase
import dev.vivvvek.seeker.Segment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val songIdxSaveKey = "song_idx"
private const val songPositionSaveKey = "song_current_position"

class PlayerController(context: Context) {
    private val audioFileDao = QueueDatabase.getDatabase(context).queueDao()
    val chapters = listOf(Segment(name = "Intro", start = 0f), Segment(name = "Part 1", start = .33f), Segment(name = "Part 2", start = .67f))
    var isInitialized = false
        private set
    var queue by mutableStateOf(listOf<AudioFile>())
        private set
    private val player = ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
        .build()
    init {
        player.addListener(object: Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { player.play()
                GlobalRepository.saveValueBackground(songIdxSaveKey, songIdx) }
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { player.play() }
        })
        CoroutineScope(Dispatchers.IO).launch {
            val savedAudioFiles = audioFileDao.getAllAudioFiles()
            val savedIdx = GlobalRepository.getValue(songIdxSaveKey, C.INDEX_UNSET).first()
            val savedPosition = GlobalRepository.getValue(songPositionSaveKey, C.TIME_UNSET).first()
            withContext(Dispatchers.Main) {
                replaceQueue(savedAudioFiles, savedIdx, savedPosition)
                isInitialized = true
            }
        }
        val handler = Handler(Looper.getMainLooper())
        lateinit var runnable: Runnable
        runnable = Runnable {
            GlobalRepository.saveValueBackground(songPositionSaveKey, player.currentPosition)
            handler.postDelayed(runnable, 1000L)
        }
        handler.postDelayed(runnable, 1000L)
    }

    val currentSong get() = queue.getOrNull(songIdx)
    val duration get() = player.duration
    val songIdx get() = player.currentMediaItemIndex
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
    fun replaceQueue(audioFiles: List<AudioFile>, initialIdx: Int, initialPosition: Long = C.TIME_UNSET) {
        CoroutineScope(Dispatchers.IO).launch {
            audioFileDao.deleteAndInsertAllAudioFiles(audioFiles)
            GlobalRepository.saveValue(songIdxSaveKey, initialIdx)
        }
        queue = audioFiles
        player.apply {
            setMediaItems(audioFiles.map { MediaItem.fromUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, it.id)) }, initialIdx, initialPosition)
            prepare()
            play()
        }
    }
    fun playPause() { player.playWhenReady = !player.isPlaying }
}