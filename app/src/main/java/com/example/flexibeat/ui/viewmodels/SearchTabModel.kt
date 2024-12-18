package com.example.flexibeat.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flexibeat.data.AudioFile
import com.example.flexibeat.data.requestFilteredAudioFiles

class SearchTabModel(context: Context): ViewModel() {
    private val contentResolver = context.contentResolver
    var searchKey by mutableStateOf("")
    var isSearching by mutableStateOf(false )
    var searchResults = mutableStateListOf<AudioFile>()

    fun search() {
        searchResults = mutableStateListOf<AudioFile>().apply { addAll(requestFilteredAudioFiles(contentResolver, searchKey)) }
    }
}