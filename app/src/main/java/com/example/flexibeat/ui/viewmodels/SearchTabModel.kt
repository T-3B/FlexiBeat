package com.example.flexibeat.ui.viewmodels

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flexibeat.data.AudioFile
import com.example.flexibeat.data.requestFilteredAudioFiles

val tagMapping = listOfNotNull(
    "album" to MediaStore.Audio.Media.ALBUM,
    "artist" to MediaStore.Audio.Media.ARTIST,
    "composer" to MediaStore.Audio.Media.COMPOSER,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "genre" to MediaStore.Audio.Media.GENRE else null,
    "title" to MediaStore.Audio.Media.TITLE,
    "tracknumber" to MediaStore.Audio.Media.TRACK,
    "year" to MediaStore.Audio.Media.YEAR,
).toMap()

class SearchTabModel(context: Context): ViewModel() {
    private val contentResolver = context.contentResolver
    private var _searchKey by mutableStateOf("")
    var searchKey get() = _searchKey
        set(value) { _searchKey = updateChips(value, true) }
    var isSearching by mutableStateOf(false )
        private set
    var errorSearchSyntax by mutableStateOf(false)
        private set
    var chips by mutableStateOf(listOf<Triple<Boolean, String, String>>())  // isNegativeFilter, tag, value
        private set
    var searchResults = mutableStateListOf<AudioFile>()
        private set

    fun search() {
        isSearching = true
        searchResults = mutableStateListOf()
        searchKey = updateChips(searchKey, false)
        if (!errorSearchSyntax)
            searchResults = mutableStateListOf<AudioFile>().apply { addAll(requestFilteredAudioFiles(contentResolver, searchKey)) }
        isSearching = false
    }

    fun chipRemoveAt(idx: Int) {
        chips = chips.toMutableList().apply { removeAt(idx) }.toList()
    }

    private fun updateChips(searchKey: String, needsSpaceSuffix: Boolean): String {
        errorSearchSyntax = false
        var tag = searchKey.substringBefore('=', "").lowercase()
        if (tag.isEmpty()) {
            if (searchKey.endsWith(' '))
                errorSearchSyntax = true
            return searchKey
        }
        val isNegativeFilter = tag.startsWith('!')
        if (isNegativeFilter)
            tag = tag.drop(1)

        tag = tagMapping.getOrDefault(tag, "")
        if (tag.isEmpty()) {
            errorSearchSyntax = true
            return searchKey
        }

        if (needsSpaceSuffix && !searchKey.endsWith(' '))
            return searchKey
        val value = searchKey.substringAfter('=').removeSuffix(" ").removeSurrounding("\"")
        if (value.any { it == '"' }) {
            errorSearchSyntax = true
            return searchKey
        }

        chips = chips.toMutableList().apply { add(Triple(isNegativeFilter, tag, value)) }.toList()
        return ""
    }
}