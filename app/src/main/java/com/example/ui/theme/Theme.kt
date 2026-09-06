package com.example.ui.theme

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

private val VibrantColorScheme = lightColorScheme(
    primary = VibrantBlue,
    onPrimary = Color.White,
    primaryContainer = VibrantBlueContainer,
    onPrimaryContainer = VibrantBlueDark,
    secondary = VibrantPurple,
    onSecondary = Color.White,
    secondaryContainer = VibrantPurpleContainer,
    onSecondaryContainer = VibrantPurpleDark,
    tertiary = VibrantPink,
    onTertiary = Color.White,
    background = VibrantBg,
    onBackground = VibrantTextPrimary,
    surface = VibrantSurface,
    onSurface = VibrantTextPrimary,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantTextSecondary,
    outline = VibrantBorder,
    outlineVariant = VibrantBorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = VibrantBlueLight,
    onPrimary = Color.White,
    primaryContainer = VibrantBlueDark,
    onPrimaryContainer = Color.White,
    secondary = VibrantPurple,
    onSecondary = Color.White,
    tertiary = VibrantPink,
    onTertiary = Color.White,
    background = DarkEditorBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkEditorSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = DarkEditorCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkEditorBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to Vibrant Palette light theme as per design HTML
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> VibrantColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
