package com.example.flexibeat.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.STORAGE_PERMISSION
import com.example.flexibeat.ui.viewmodels.WithStoragePermissionModel

@Composable
fun WithStoragePermission(body: @Composable () -> Unit) {
    val context = LocalContext.current
    val withStoragePermissionModel = viewModel { WithStoragePermissionModel(context) }
    if (!withStoragePermissionModel.isGranted) {
        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { withStoragePermissionModel.isGranted = it }
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { permissionLauncher.launch(STORAGE_PERMISSION) }, Modifier.align(Alignment.Center)) {
                Text("Request Read Storage Permission")
            }
        }
    } else
        body()
}