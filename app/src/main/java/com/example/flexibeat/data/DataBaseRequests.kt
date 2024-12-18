package com.example.flexibeat.data

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException

fun requestFilteredAudioFiles(contentResolver: ContentResolver, searchKey: String): List<AudioFile> {
    throw NotImplementedError()
}

fun fetchFileExplorerItems(contentResolver: ContentResolver, relPath: File): FileExplorerItems {
    val folders = mutableSetOf<File>()
    if (relPath.path.isNotEmpty())
        folders.add(File(relPath.path, ".."))
    val audioFiles = mutableListOf<AudioFile>()
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.RELATIVE_PATH,
        MediaStore.Audio.Media.DISPLAY_NAME
    )
    val selection = "${MediaStore.Files.FileColumns.RELATIVE_PATH} like ? "
    val selectionArgs = arrayOf(relPath.path + "%")
    val sortOrder = MediaStore.Audio.Media.TITLE
    contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use {
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

            if (relPath == fileRelPath) {
                val id = it.getLong(idIdx)
                val title = it.getString(titleIdx)
                val album = it.getString(albumIdx)
                val albumId = it.getLong(albumIdIdx)
                val artist = it.getString(artistIdx)
                val duration = it.getLong(durationIdx)
                val displayName = it.getString(displayNameIdx)

                val albumArt = fetchAlbumArt(contentResolver, albumId, File(File(Environment.getExternalStorageDirectory(), fileRelPath.path), displayName).path)
                audioFiles.add(AudioFile(id, title, album, artist, albumArt, duration))
            }

            fileRelPath.toRelativeString(relPath).substringBefore('/').takeIf(String::isNotEmpty)?.let { folders.add(if (relPath.path.isEmpty()) File(it) else File(relPath.path, it)) }

        }

    }
    return FileExplorerItems(folders.sorted(), audioFiles)
}

fun fetchAlbumArt(contentResolver: ContentResolver, albumId: Long, songAbsolutePath: String): Bitmap? {
    val size = android.util.Size(100, 100) // Set the desired thumbnail size
    var coverArt: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {try {
        contentResolver.loadThumbnail(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId), size, null)
    } catch (e: IOException) {
        null
    }} else {
        var albumArtPath: String? = null
        contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Albums.ALBUM_ART), "${MediaStore.Audio.Albums._ID} = ?", arrayOf(albumId.toString()), null)?.use {
            if (it.moveToFirst()) albumArtPath = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
        }
        BitmapFactory.decodeFile(albumArtPath)
    }
    return coverArt
    @Suppress("UNREACHABLE_CODE")
    if (coverArt == null) {  // TODO too slow
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(songAbsolutePath)
        val data = retriever.embeddedPicture
        retriever.release()
        if (data != null)
            coverArt = BitmapFactory.decodeByteArray(data, 0, data.size)
        else {
            // Fallback: Check for image files in the song's directory
            val songFile = File(songAbsolutePath)
            val directory = songFile.parentFile ?: return null

            // Filter images with names "cover" or "folder" (case-insensitive)
            val imageFiles = directory.listFiles { file ->
                val mimeType = file.toURI().toURL().openConnection().contentType
                val isImage = mimeType.startsWith("image/")
                val fileNameWithoutExtension = file.nameWithoutExtension.lowercase()
                isImage && (fileNameWithoutExtension == "cover" || fileNameWithoutExtension == "folder")
            } ?: return null

            // If found, decode the first match
            return if (imageFiles.isNotEmpty()) {
                BitmapFactory.decodeFile(imageFiles[0].absolutePath)
            } else {
                null // No suitable image found
            }
        }
    }
    return coverArt
}