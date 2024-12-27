package com.example.flexibeat.ui.viewmodels

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexibeat.data.AudioFile
import com.example.flexibeat.data.SEARCH_ANY_METADATA
import com.example.flexibeat.data.requestFilteredAudioFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        set(value) { _searchKey = updateChips(value, false) }
    var isSearching by mutableStateOf(false)
        private set
    var errorSearchFilters by mutableStateOf(false)
        private set
    var chips by mutableStateOf(listOf<Triple<Boolean, String, String>>())  // isNegativeFilter, tag, value
        private set
    var searchResults by mutableStateOf(listOf<AudioFile>())
        private set

    fun search() {
        isSearching = true
        searchResults = listOf()
        searchKey = updateChips(searchKey, true)
        if (!errorSearchFilters) {
            viewModelScope.launch {
                val audioFiles = requestFilteredAudioFiles(contentResolver, chips)
                withContext(Dispatchers.Main) {
                    searchResults = audioFiles
                    isSearching = false
                }
            }
        }
    }

    fun chipRemoveAt(idx: Int) {
        chips = chips.toMutableList().apply { removeAt(idx) }.toList()
    }

    private fun updateChips(searchKey: String, force: Boolean): String {
        errorSearchFilters = false
        if (searchKey.isBlank())
            return ""
        val defaultTag = if (force) SEARCH_ANY_METADATA else ""
        var tag = searchKey.substringBefore('=', defaultTag).lowercase()
        if (tag.isEmpty()) {
            if (searchKey.endsWith(' '))
                errorSearchFilters = true
            return searchKey
        }
        val isNegativeFilter = tag.startsWith('!')
        if (isNegativeFilter)
            tag = tag.drop(1)

        tag = tagMapping.getOrDefault(tag, defaultTag)
        if (tag.isEmpty() || chips.any { it.second == tag }) {
            errorSearchFilters = true
            return searchKey
        }

        if (!force && !searchKey.endsWith(' '))
            return searchKey
        val value = searchKey.substringAfter('=').removeSuffix(" ").removeSurrounding("\"")
        if (value.any { it == '"' }) {
            errorSearchFilters = true
            return searchKey
        }

        chips = chips.toMutableList().apply { add(Triple(isNegativeFilter, tag, value)) }.toList()
        return ""
    }
}