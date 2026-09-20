package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Jet Black tones
val PitchBlack = Color(0xFF030304)
val ObsidianBlack = Color(0xFF08080C)
val DarkGlass = Color(0xCC0D0D14)
val GlassSurface = Color(0x9914141E)
val GlassSurfaceLight = Color(0x33FFFFFF)
val GlassBorder = Color(0x2EFFFFFF)

// Chrome & Metallic tones
val ChromeHighlight = Color(0xFFFFFFFF)
val ChromeBright = Color(0xFFECEFF4)
val ChromeMid = Color(0xFFB0B3C0)
val ChromeDark = Color(0xFF5A5E70)
val ChromeEdge = Color(0x55B0B3C0)

// Neon & Accent Colors
val CrimsonNeon = Color(0xFFFF1E56)
val CrimsonDeep = Color(0xFF8B0024)
val CrimsonGlow = Color(0x66FF1E56)

val VioletNeon = Color(0xFFB026FF)
val VioletDeep = Color(0xFF4A0E78)
val VioletGlow = Color(0x66B026FF)

val CyanNeon = Color(0xFF00F0FF)
val CyanGlow = Color(0x6600F0FF)

val AmberWarm = Color(0xFFFF9F1C)

// Gradients
val ChromeGradient = Brush.linearGradient(
    colors = listOf(ChromeHighlight, ChromeMid, ChromeDark, ChromeBright)
)

val CrimsonGlassGradient = Brush.linearGradient(
    colors = listOf(Color(0x33FF1E56), Color(0x0DFF1E56), Color(0x00000000))
)

val ObsidianGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0E0E14), Color(0xFF050508), PitchBlack)
)
