package com.example.flexibeat.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.STORAGE_PERMISSION
import com.example.flexibeat.ui.viewmodels.WithStoragePermissionModel

@Composable
fun WithStoragePermission(body: @Composable () -> Unit) {
    val context = LocalContext.current
    val withStoragePermissionModel = viewModel { WithStoragePermissionModel(context) }
    if (!withStoragePermissionModel.isGranted) {
        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { withStoragePermissionModel.isGranted = it }
        Button(onClick = { permissionLauncher.launch(STORAGE_PERMISSION) }) {
            Text("Request Storage Permission")
        }
    } else
        body()
}