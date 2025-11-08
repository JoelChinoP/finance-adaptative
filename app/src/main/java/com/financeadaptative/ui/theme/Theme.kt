package com.financeadaptative.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Color(0xFF16A34A), // verde FINPER
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF062911),
    secondary = Color(0xFF0891B2), // cian
    onSecondary = Color(0xFF001F26),
    tertiary = Color(0xFFF59E0B), // ámbar
    onTertiary = Color(0xFF261A00),
    background = Color(0xFFF7F7F5),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE6EAE7),
    onSurface = Color(0xFF111311)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF22C55E), // verde FINPER brillante
    onPrimary = Color(0xFF00210E),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFBCF7D5),
    secondary = Color(0xFF38BDF8), // cian
    onSecondary = Color(0xFF001318),
    tertiary = Color(0xFFF59E0B), // ámbar
    onTertiary = Color(0xFF221600),
    background = Color(0xFF0B0F0D),
    surface = Color(0xFF111512),
    surfaceVariant = Color(0xFF1A1F1C),
    onSurface = Color(0xFFE7ECE8)
)

@Composable
fun FinPerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
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