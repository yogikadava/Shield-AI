package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberBluePrimary,
    secondary = CyberBlueSecondary,
    tertiary = CyberAccentCyan,
    background = CyberDeepNavy,
    surface = CyberCardBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = CyberDarkBg,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardBg,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberRedAlert,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyberBluePrimary,
    secondary = CyberBlueSecondary,
    tertiary = CyberAccentCyan,
    background = Color(0xFFF8FAFC), // A lighter tech slate
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    error = CyberRedAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default for the cyber aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
