package com.example.flexibeat.data

import android.graphics.Bitmap
import java.io.File

data class AudioFile(
    val id: Long,
    val title: CharSequence?,
    val album: CharSequence?,
    val artist: CharSequence?,
    val coverArt: Bitmap?,
    val duration: Long?
)

data class FileExplorerItems(
    val folders: List<File>,
    val files: List<AudioFile>
)