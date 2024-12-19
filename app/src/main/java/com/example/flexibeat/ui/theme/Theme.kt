package com.example.flexibeat.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.flexibeat.data.datasave.GlobalRepository
import com.example.flexibeat.ui.views.MusicPlayerMainScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val primaryColorSaveKey = "primary_color"

object DefaultColors {
    val primary = Color(0xFFFF8C00)
}

object UserColors {
    private var _primary by mutableStateOf(DefaultColors.primary)
    var primary get() = _primary
        set(value) {
            _primary = value
            pushDataSave()
        }

    init { getDataSave() }

    private fun getDataSave() {
        CoroutineScope(Dispatchers.IO).launch {
            val savedPrimary = Color(GlobalRepository.getValue(primaryColorSaveKey, DefaultColors.primary.toArgb()).first())
            withContext(Dispatchers.Main) {
                _primary = savedPrimary
            }
        }
    }
    private fun pushDataSave() {
        CoroutineScope(Dispatchers.IO).launch {
            GlobalRepository.saveValue(primaryColorSaveKey, _primary.toArgb())
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, device = "spec:parent=2.7in QVGA slider,orientation=portrait")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FlexiBeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // Dynamic color is available on Android 12+
    content: @Composable () -> Unit = { MusicPlayerMainScreen() }
) {
    val context = LocalContext.current
    GlobalRepository.initialize(context)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> darkColorScheme(background = Color.Black)
        else -> lightColorScheme()
    }.copy(primary = UserColors.primary)
    rememberSystemUiController().apply {
        setStatusBarColor(colorScheme.background, !darkTheme)
        setNavigationBarColor(colorScheme.background, !darkTheme)
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}