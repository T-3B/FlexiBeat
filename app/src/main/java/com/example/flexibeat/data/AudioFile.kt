package com.example.flexibeat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

internal const val AUDIOFILE_DBKEY = "audio_file"

@Entity(tableName = AUDIOFILE_DBKEY)
data class AudioFile(
    @PrimaryKey val id: Long,
    val title: String?,
    val album: String?,
    val artist: String?,
    val duration: Long?,
    val albumArtUri: String?
)

data class FileExplorerItems(
    val folders: List<File>,
    val files: List<AudioFile>
)