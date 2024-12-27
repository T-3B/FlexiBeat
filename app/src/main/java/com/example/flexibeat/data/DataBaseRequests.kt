package com.example.flexibeat.data

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import java.io.File

const val SEARCH_ANY_METADATA = "FlexiBeat_Search_Any_Metadata"

private val baseURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.RELATIVE_PATH,
    MediaStore.Audio.Media.DISPLAY_NAME
)
val searchableColumns = listOf(
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.YEAR,
)

fun requestFilteredAudioFiles(contentResolver: ContentResolver, filters: List<Triple<Boolean, String, String>>): List<AudioFile> {
    val audioFiles = mutableListOf<AudioFile>()
    val selection = filters.joinToString(" AND ") {
        if (it.second == SEARCH_ANY_METADATA)
            searchableColumns.joinToString(" OR ", "(", ")") { "$it LIKE ?" }
        else
            "${it.second} ${if (it.first) "NOT" else ""} LIKE ?"
    }
    val selectionArgs = filters.flatMap { List(if (it.second == SEARCH_ANY_METADATA) searchableColumns.size else 1) { _ -> "%${it.third}%" } }.toTypedArray()
    val sortOrder = MediaStore.Audio.Media.TITLE
    contentResolver.query(baseURI, projection, selection, selectionArgs, sortOrder)?.use {
        val idIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val albumIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val artistIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        while (it.moveToNext()) {
            val id = it.getLong(idIdx)
            val title = it.getString(titleIdx)
            val album = it.getString(albumIdx)
            val albumId = it.getLong(albumIdIdx)
            val artist = it.getString(artistIdx)
            val duration = it.getLong(durationIdx)

            val albumArt = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)

            audioFiles.add(AudioFile(id, title, album, artist, duration, albumArt.toString()))
        }

    }
    return audioFiles
}

@OptIn(UnstableApi::class)
fun fetchFileExplorerItems(contentResolver: ContentResolver, currentDir: File): FileExplorerItems {
    val folders = mutableSetOf<File>()
    if (currentDir.path.isNotEmpty())
        folders.add(File(currentDir.path, ".."))
    val audioFiles = mutableListOf<AudioFile>()

    val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf(currentDir.path + "%")
    val sortOrder = "${MediaStore.Audio.Media.RELATIVE_PATH} ASC, ${MediaStore.Audio.Media.TITLE} ASC"
    contentResolver.query(baseURI, projection, selection, selectionArgs, sortOrder)?.use {
        val idIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val albumIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val artistIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val fileRelPathIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
        val displayNameIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        while (it.moveToNext()) {
            val fileRelPath = File(it.getString(fileRelPathIdx).let { if (it == "/") "" else it })

            if (currentDir == fileRelPath) {
                val id = it.getLong(idIdx)
                val title = it.getString(titleIdx)
                val album = it.getString(albumIdx)
                val albumId = it.getLong(albumIdIdx)
                val artist = it.getString(artistIdx)
                val duration = it.getLong(durationIdx)
                val displayName = it.getString(displayNameIdx)

                val albumArt = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)

                audioFiles.add(AudioFile(id, title, album, artist, duration, albumArt.toString()))
            }
            else
                fileRelPath.toRelativeString(currentDir).substringBefore('/').let { folders.add(File(currentDir.path, it)) }

        }

    }
    return FileExplorerItems(folders.toList(), audioFiles)
}