package com.example.flexibeat.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import com.example.flexibeat.data.FileExplorerItems
import com.example.flexibeat.data.fetchFileExplorerItems
import com.example.flexibeat.STORAGE_PERMISSION
import java.io.File

class LocalStorageModel(context: Context) : ViewModel() {
    private val contentResolver = context.contentResolver
    var hasStorageAccess by mutableStateOf(ContextCompat.checkSelfPermission(context, STORAGE_PERMISSION) == PermissionChecker.PERMISSION_GRANTED)
        private set
    var items by mutableStateOf(FileExplorerItems(emptyList(), emptyList()))
        private set
    var relPath by mutableStateOf(File(""))
        private set

    fun updateRelPath(new: File = relPath) {
        relPath = new.normalize() // Normalize here
        items = fetchFileExplorerItems(contentResolver, relPath)
    }
}