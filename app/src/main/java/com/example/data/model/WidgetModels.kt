package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.*

enum class WidgetType(val title: String, val subtitle: String, val defaultEnabled: Boolean) {
    CLOCK("Digital Clock", "Oversized minimalist typography", true),
    ANALOG_3D("3D Chrono Dial", "3D luxury metallic chronometer", true),
    CASSETTE_3D("3D Cassette Tape", "3D floating cassette tape clock", true),
    NOW_PLAYING("Now Playing", "Album art & Spotify-style controls", true),
    AUDIO_VISUALIZER("Audio Visualizer", "Dynamic reactive frequency bars", true),
    LYRICS("Scrolling Lyrics", "Synced mood lyrics card", true),
    COUNTDOWN("Neon Countdown", "Late-night drop & tour timer", true),
    DATE_TRACKER("Date & Late Night", "Lunar phase & hours until sunrise", true),
    MOOD_ATMOSPHERE("Mood & Atmosphere", "Soundscape & animated pulse", true),
    ALBUM_SHOWCASE("Album Showcase", "Interactive vinyl & cover showcase", true),
    MOOD_QUOTES("Atlantic Mood Quotes", "Minimalist aesthetic mood text", true),
    NIGHT_MODE("AMOLED Mode", "Ultra-dark pure black canvas", true)
}

enum class ClockStyle(val displayName: String) {
    OVERSIZED_DIGITAL("Oversized Digital"),
    MINIMAL_ANALOG("Minimal Luxury Analog"),
    NEON_CYBER("Neon Cyber Grid"),
    CHROME_STACK("Chrome Stack")
}

enum class ThemeAccent(
    val displayName: String,
    val primary: Color,
    val glow: Color,
    val secondary: Color
) {
    CRIMSON("Deep Crimson", CrimsonNeon, CrimsonGlow, CrimsonDeep),
    VIOLET("Neon Violet", VioletNeon, VioletGlow, VioletDeep),
    CHROME("Liquid Chrome", ChromeBright, ChromeEdge, ChromeDark),
    CYAN("Electric Cyan", CyanNeon, CyanGlow, Color(0xFF005F73)),
    EMERALD("Neon Emerald", Color(0xFF00FF87), Color(0x4000FF87), Color(0xFF00381E)),
    GOLD("Noir Amber Gold", Color(0xFFFFB300), Color(0x40FFB300), Color(0xFF4A3400)),
    PINK("Hot Neon Pink", Color(0xFFFF007F), Color(0x40FF007F), Color(0xFF4A0025))
}

enum class AmbientMode(val displayName: String) {
    SMOKE_GLASS("Obsidian Glass Smoke"),
    PURE_AMOLED("Pure AMOLED Black"),
    LIQUID_PULSE("Liquid Neon Pulse")
}

data class LyricLine(
    val timestampMs: Long,
    val text: String,
    val subtext: String = ""
)

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    @DrawableRes val coverRes: Int,
    val durationSeconds: Int,
    val lyrics: List<LyricLine>,
    val soundProfileVibe: String
)

data class QuoteItem(
    val id: String,
    val quote: String,
    val source: String,
    val moodTag: String
)

data class SoundscapeItem(
    val id: String,
    val name: String,
    val iconName: String,
    val frequencyLabel: String
)
