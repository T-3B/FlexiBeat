package com.example.flexibeat

import android.Manifest
import android.os.Build

val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO  else Manifest.permission.READ_EXTERNAL_STORAGE