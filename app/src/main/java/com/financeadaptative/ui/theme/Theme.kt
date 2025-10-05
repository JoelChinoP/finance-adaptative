package com.financeadaptative.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Color(0xFF006685),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC1E8FF),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = Color(0xFF4B626E),
    background = Color(0xFFF8FBFD),
    surface = Color(0xFFFDFDFD)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF63D2FF),
    onPrimary = Color(0xFF003546),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFC1E8FF),
    secondary = Color(0xFFB5C9D4),
    background = Color(0xFF0F1416),
    surface = Color(0xFF14191B)
)

@Composable
fun FinPerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val scheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val ctx = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        } else {
            if (darkTheme) DarkScheme else LightScheme
        }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}