package com.example.flexibeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.flexibeat.ui.theme.FlexiBeatTheme
import com.example.flexibeat.ui.views.MusicPlayerMainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FlexiBeatTheme(dynamicColor = true) { MusicPlayerMainScreen() } }
    }
}