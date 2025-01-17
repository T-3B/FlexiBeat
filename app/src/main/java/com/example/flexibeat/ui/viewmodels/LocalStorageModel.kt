package com.example.flexibeat.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexibeat.data.FileExplorerItems
import com.example.flexibeat.data.datasave.GlobalRepository
import com.example.flexibeat.data.fetchFileExplorerItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val relPathSaveKey = "rel_path"

class LocalStorageModel(context: Context) : ViewModel() {
    private val contentResolver = context.contentResolver
    var items by mutableStateOf(FileExplorerItems(emptyList(), emptyList()))
        private set
    private var _relPath by mutableStateOf(File(""))
    var relPath get() = _relPath
        set(value) {
            _relPath = value.normalize()
            GlobalRepository.saveValueBackground(relPathSaveKey, _relPath.path)
            updateItems()
        }

    init {
        GlobalRepository.getValueBackground(relPathSaveKey, "") { relPath = File(it) }
    }

    private fun updateItems() {
        viewModelScope.launch {
            items = withContext(Dispatchers.IO) { fetchFileExplorerItems(contentResolver, relPath) }
        }
    }
}