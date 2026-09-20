package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CassetteDarkColorScheme = darkColorScheme(
    primary = CrimsonNeon,
    onPrimary = Color.White,
    primaryContainer = CrimsonDeep,
    onPrimaryContainer = ChromeBright,
    secondary = VioletNeon,
    onSecondary = Color.White,
    secondaryContainer = VioletDeep,
    onSecondaryContainer = ChromeBright,
    tertiary = ChromeBright,
    onTertiary = PitchBlack,
    tertiaryContainer = ChromeDark,
    onTertiaryContainer = ChromeHighlight,
    background = PitchBlack,
    onBackground = ChromeBright,
    surface = ObsidianBlack,
    onSurface = ChromeBright,
    surfaceVariant = DarkGlass,
    onSurfaceVariant = ChromeMid,
    outline = GlassBorder,
    outlineVariant = Color(0x1AFFFFFF)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CassetteDarkColorScheme,
        typography = Typography,
        content = content
    )
}
