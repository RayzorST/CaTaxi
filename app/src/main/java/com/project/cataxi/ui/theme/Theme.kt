package com.project.cataxi.ui.theme

import android.app.Activity
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

private val LightColorScheme = darkColorScheme(
    onTertiary = White0,
    background = White0,
    secondary = White,
    onSecondary = Black,
    tertiary = Red,
    onSurface = Black
)

private val DarkColorScheme = lightColorScheme(
    primary = Red,
    inversePrimary = White0,
    onPrimary = White0,
    background = DarkGray,
    onBackground = White0,
    secondary = Gray,
    onSecondary = White0,
    tertiary = Red,
    onSurface = White0
)

@Composable
fun CaTaxiTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}