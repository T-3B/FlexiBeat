package com.example.flexibeat.ui.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.flexibeat.ui.theme.DefaultColors
import com.example.flexibeat.ui.theme.UserColors
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import io.mhssn.colorpicker.ext.toHex

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferencesTab() {
    var showDialog by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Primary color:", Modifier.weight(1f), fontSize = 24.sp)
                Button({ showDialog = true }, colors = ButtonColors(Color.Unspecified, colorScheme.onBackground, Color.Unspecified, Color.Unspecified)) {
                    Text(colorScheme.primary.toHex(true), fontSize = 24.sp)
                    Icon(Icons.Default.Circle, "Primary color preview", Modifier.size(48.dp), colorScheme.primary)
                }
                IconButton({ UserColors.primary = DefaultColors.primary }) {
                    Icon(Icons.Default.Delete, "Primary color reset", Modifier.size(48.dp))
                }
            }
        }
    }
    ColorPickerDialog(
        show = showDialog,
        type = ColorPickerType.Circle(showAlphaBar = false),
        properties = DialogProperties(),
        onDismissRequest = { showDialog = false },
        onPickedColor = { showDialog = false; UserColors.primary = it },
    )
}