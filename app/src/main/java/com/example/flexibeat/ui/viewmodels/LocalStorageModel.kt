package com.example.flexibeat.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flexibeat.data.FileExplorerItems
import com.example.flexibeat.data.fetchFileExplorerItems
import java.io.File

class LocalStorageModel(context: Context) : ViewModel() {
    private val contentResolver = context.contentResolver
    var items by mutableStateOf(FileExplorerItems(emptyList(), emptyList()))
        private set
    private var _relPath by mutableStateOf(File(""))
    var relPath get() = _relPath
        set(value) {
            _relPath = value.normalize()
            items = fetchFileExplorerItems(contentResolver, relPath)
        }

    init { items = fetchFileExplorerItems(contentResolver, relPath) }
}