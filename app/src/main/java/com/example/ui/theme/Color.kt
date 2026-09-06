package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Vibrant Palette - Core Theme Colors
val VibrantBg = Color(0xFFF0F4F9)           // Fresh light cloud canvas
val VibrantSurface = Color(0xFFFFFFFF)      // Pure white card surfaces
val VibrantSurfaceVariant = Color(0xFFF8FAFC)
val VibrantBorder = Color(0xFFE2E8F0)       // Slate 200 clean borders
val VibrantBorderLight = Color(0xFFF1F5F9)  // Slate 100 subtle divider

val VibrantTextPrimary = Color(0xFF0F172A)  // Slate 900
val VibrantTextSecondary = Color(0xFF334155) // Slate 700
val VibrantTextMuted = Color(0xFF64748B)    // Slate 500
val VibrantTextSubtle = Color(0xFF94A3B8)   // Slate 400

// Vibrant Colors & Accents
val VibrantBlue = Color(0xFF2563EB)         // Blue 600
val VibrantBlueLight = Color(0xFF3B82F6)    // Blue 500
val VibrantBlueContainer = Color(0xFFDBEAFE) // Blue 100
val VibrantBlueDark = Color(0xFF1D4ED8)     // Blue 700

val VibrantPurple = Color(0xFFA855F7)       // Purple 500
val VibrantPurpleContainer = Color(0xFFF3E8FF) // Purple 100
val VibrantPurpleDark = Color(0xFF7E22CE)   // Purple 700

val VibrantPink = Color(0xFFF472B6)         // Pink 400
val VibrantRed = Color(0xFFF87171)          // Red 400
val VibrantRedLight = Color(0xFFFEE2E2)     // Red 100
val VibrantGreen = Color(0xFF10B981)        // Emerald 500
val VibrantGreenLight = Color(0xFFD1FAE5)   // Emerald 100
val VibrantAmber = Color(0xFFF59E0B)        // Amber 500
val VibrantAmberLight = Color(0xFFFEF3C7)   // Amber 100

// Signature Gemini / Vibrant Gradient
val VibrantGeminiGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF3B82F6), // Blue 500
        Color(0xFFA855F7), // Purple 500
        Color(0xFFF87171)  // Red 400
    )
)

val VibrantGeminiGradientReversed = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2563EB),
        Color(0xFF9333EA),
        Color(0xFFEC4899)
    )
)

// Legacy / Editor support
val NetBeansBlue = VibrantBlue
val NetBeansBlueDark = VibrantBlueDark
val NetBeansCyan = Color(0xFF00B4D8)
val NetBeansAccent = VibrantBlueLight

val DarkEditorBackground = Color(0xFF0F172A)
val DarkEditorSurface = Color(0xFF1E293B)
val DarkEditorCard = Color(0xFF26354D)
val DarkEditorBorder = Color(0xFF334155)

// Code Syntax Colors (Vibrant High-Contrast)
val SyntaxKeyword = Color(0xFF3B82F6)
val SyntaxType = Color(0xFF06B6D4)
val SyntaxString = Color(0xFF10B981)
val SyntaxNumber = Color(0xFFF43F5E)
val SyntaxComment = Color(0xFF94A3B8)
val SyntaxFunction = Color(0xFFA855F7)
val SyntaxAnnotation = Color(0xFFF59E0B)

// Status Colors
val TerminalGreen = Color(0xFF10B981)
val TerminalRed = Color(0xFFEF4444)
val TerminalWarning = Color(0xFFF59E0B)
val GeminiPurple = VibrantPurple
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)
val GoogleBlue = Color(0xFF4285F4)
