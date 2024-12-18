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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flexibeat.R
import com.example.flexibeat.ui.views.MusicPlayerMainScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, device = "spec:parent=2.7in QVGA slider,orientation=portrait")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FlexiBeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // Dynamic color is available on Android 12+
    content: @Composable () -> Unit = { MusicPlayerMainScreen() }
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }.copy(primary = colorResource(R.color.orange), background = Color.Black)
    rememberSystemUiController().apply {
        setStatusBarColor(colorScheme.background, !darkTheme)
        setNavigationBarColor(colorScheme.background, !darkTheme)
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}