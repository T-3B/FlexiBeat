package com.example.flexibeat.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import com.example.flexibeat.STORAGE_PERMISSION

class WithStoragePermissionModel(context: Context): ViewModel() {
    var isGranted by mutableStateOf(ContextCompat.checkSelfPermission(context, STORAGE_PERMISSION) == PermissionChecker.PERMISSION_GRANTED)
}