package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockStyle
import com.example.data.model.WidgetType
import com.example.ui.components.AmbientBackground
import com.example.ui.components.PinWidgetsBanner
import com.example.ui.components.QuickCustomizeSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.CassetteViewModel
import com.example.ui.widgets.*

enum class MainHubTab(val label: String, val badge: String) {
    DECK("DECK", "AUDIO & TIME"),
    WIDGETS("WIDGETS", "7 HOMESCREEN"),
    STUDIO("STUDIO", "AMBIENT & FX")
}

@Composable
fun MainScreen(
    viewModel: CassetteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(MainHubTab.DECK) }
    var selectedClockTab by remember { mutableStateOf(0) } // 0: Digital, 1: 3D Chrono, 2: 3D Cassette

    AmbientBackground(
        ambientMode = uiState.ambientMode,
        themeAccent = uiState.themeAccent,
        isNightModeOnly = uiState.isNightModeOnly,
        modifier = modifier
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HubTopBar(
                        isLateNight = uiState.timeState.isLateNight,
                        accentColor = uiState.themeAccent.primary,
                        isNightMode = uiState.isNightModeOnly,
                        onToggleNightMode = { viewModel.toggleNightMode() },
                        onOpenCustomize = { viewModel.setCustomizerSheetVisible(true) }
                    )

                    // Clean Segmented Mode Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(12.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MainHubTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        if (isSelected) uiState.themeAccent.primary.copy(alpha = 0.22f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) uiState.themeAccent.primary else Color.Transparent,
                                        shape = RoundedCornerShape(9.dp)
                                    )
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.label,
                                    color = if (isSelected) ChromeHighlight else ChromeMid,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.2.sp
                                )
                            }
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("widget_hub_scroll"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    MainHubTab.DECK -> {
                        // Clean unified Clock Switcher + Clock Display
                        item(key = "clock_section") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Clock mode selector pills
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x10FFFFFF))
                                        .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(10.dp))
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("DIGITAL", "3D CHRONO", "3D CASSETTE").forEachIndexed { index, title ->
                                        val isSel = selectedClockTab == index
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSel) uiState.themeAccent.primary.copy(alpha = 0.25f)
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    width = if (isSel) 1.dp else 0.dp,
                                                    color = if (isSel) uiState.themeAccent.primary else Color.Transparent,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedClockTab = index }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = title,
                                                color = if (isSel) ChromeHighlight else ChromeDark,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                letterSpacing = 0.8.sp
                                            )
                                        }
                                    }
                                }

                                // Selected Clock Display
                                when (selectedClockTab) {
                                    0 -> {
                                        ClockWidget(
                                            timeState = uiState.timeState,
                                            clockStyle = uiState.clockStyle,
                                            themeAccent = uiState.themeAccent,
                                            onCycleStyle = {
                                                val nextStyle = when (uiState.clockStyle) {
                                                    ClockStyle.OVERSIZED_DIGITAL -> ClockStyle.MINIMAL_ANALOG
                                                    ClockStyle.MINIMAL_ANALOG -> ClockStyle.NEON_CYBER
                                                    ClockStyle.NEON_CYBER -> ClockStyle.CHROME_STACK
                                                    ClockStyle.CHROME_STACK -> ClockStyle.OVERSIZED_DIGITAL
                                                }
                                                viewModel.setClockStyle(nextStyle)
                                            }
                                        )
                                    }
                                    1 -> {
                                        Analog3DWidget(
                                            timeState = uiState.timeState,
                                            themeAccent = uiState.themeAccent
                                        )
                                    }
                                    2 -> {
                                        Cassette3DWidget(
                                            timeState = uiState.timeState,
                                            themeAccent = uiState.themeAccent
                                        )
                                    }
                                }
                            }
                        }

                        // NOW PLAYING WIDGET
                        item(key = "now_playing") {
                            NowPlayingWidget(
                                track = uiState.currentTrack,
                                isPlaying = uiState.isPlaying,
                                currentPositionMs = uiState.currentPositionMs,
                                isFavorite = uiState.isFavoriteSong,
                                isShuffle = uiState.isShuffle,
                                isRepeat = uiState.isRepeat,
                                themeAccent = uiState.themeAccent,
                                onTogglePlay = { viewModel.togglePlayPause() },
                                onNext = { viewModel.nextTrack() },
                                onPrevious = { viewModel.previousTrack() },
                                onSeek = { viewModel.seekToFraction(it) },
                                onToggleFavorite = { viewModel.toggleFavoriteSong() },
                                onToggleShuffle = { viewModel.toggleShuffle() },
                                onToggleRepeat = { viewModel.toggleRepeat() }
                            )
                        }

                        // SCROLLING LYRICS WIDGET
                        if (uiState.enabledWidgets.contains(WidgetType.LYRICS)) {
                            item(key = "lyrics") {
                                LyricsWidget(
                                    track = uiState.currentTrack,
                                    currentLyricIndex = uiState.currentLyricIndex,
                                    themeAccent = uiState.themeAccent,
                                    onSelectLyric = { viewModel.jumpToLyric(it) }
                                )
                            }
                        }

                        // CHASE ATLANTIC MOOD QUOTES
                        if (uiState.enabledWidgets.contains(WidgetType.MOOD_QUOTES)) {
                            item(key = "quote") {
                                QuoteWidget(
                                    quote = uiState.currentQuote,
                                    themeAccent = uiState.themeAccent,
                                    isCopiedNotification = uiState.isCopiedNotification,
                                    onShuffle = { viewModel.shuffleQuote() },
                                    onCopy = { viewModel.copyQuoteToClipboard(it) }
                                )
                            }
                        }
                    }

                    MainHubTab.WIDGETS -> {
                        // HOMESCREEN WIDGET STUDIO (Pinning, Themes, Custom Colors)
                        item(key = "pin_banner") {
                            PinWidgetsBanner(themeAccent = uiState.themeAccent)
                        }
                    }

                    MainHubTab.STUDIO -> {
                        // AUDIO VISUALIZER
                        item(key = "visualizer") {
                            VisualizerWidget(
                                levels = uiState.visualizerLevels,
                                waveformPoints = uiState.waveformPoints,
                                isPlaying = uiState.isPlaying,
                                themeAccent = uiState.themeAccent
                            )
                        }

                        // MOOD & SOUNDSCAPES
                        item(key = "mood_atmosphere") {
                            MoodAtmosphereWidget(
                                soundscapes = viewModel.allSoundscapes,
                                activeSoundscapeId = uiState.activeSoundscapeId,
                                isPlaying = uiState.isSoundscapePlaying,
                                volume = uiState.soundscapeVolume,
                                themeAccent = uiState.themeAccent,
                                onToggleSoundscape = { viewModel.toggleSoundscape(it) },
                                onVolumeChange = { viewModel.setSoundscapeVolume(it) }
                            )
                        }

                        // COUNTDOWN
                        item(key = "countdown") {
                            CountdownWidget(
                                title = uiState.countdownTitle,
                                remaining = uiState.countdownRemaining,
                                themeAccent = uiState.themeAccent,
                                onSelectPreset = { title, target ->
                                    viewModel.setCountdown(title, target)
                                }
                            )
                        }

                        // DATE & LATE-NIGHT TRACKER
                        item(key = "date_tracker") {
                            DateTrackerWidget(
                                timeState = uiState.timeState,
                                themeAccent = uiState.themeAccent
                            )
                        }

                        // ALBUM SHOWCASE
                        item(key = "album_showcase") {
                            AlbumShowcaseWidget(
                                tracks = viewModel.allTracks,
                                currentTrackId = uiState.currentTrack.id,
                                themeAccent = uiState.themeAccent,
                                onSelectTrack = { viewModel.selectTrack(it) }
                            )
                        }

                        // NIGHT MODE TOGGLE
                        item(key = "night_mode") {
                            NightModeWidget(
                                isNightModeOnly = uiState.isNightModeOnly,
                                themeAccent = uiState.themeAccent,
                                onToggleNightMode = { viewModel.toggleNightMode() }
                            )
                        }
                    }
                }

                // Footer signature
                item(key = "footer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CASSETTE // ATLANTIC",
                            color = ChromeDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "DARK LUXURY WIDGET DECK",
                            color = Color(0x33FFFFFF),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // Quick Customization Bottom Sheet
        if (uiState.showCustomizerSheet) {
            QuickCustomizeSheet(
                enabledWidgets = uiState.enabledWidgets,
                clockStyle = uiState.clockStyle,
                themeAccent = uiState.themeAccent,
                ambientMode = uiState.ambientMode,
                onToggleWidget = { viewModel.toggleWidget(it) },
                onSelectClockStyle = { viewModel.setClockStyle(it) },
                onSelectThemeAccent = { viewModel.setThemeAccent(it) },
                onSelectAmbientMode = { viewModel.setAmbientMode(it) },
                onDismiss = { viewModel.setCustomizerSheetVisible(false) }
            )
        }
    }
}

@Composable
private fun HubTopBar(
    isLateNight: Boolean,
    accentColor: Color,
    isNightMode: Boolean,
    onToggleNightMode: () -> Unit,
    onOpenCustomize: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = "CASSETTE",
                    color = ChromeHighlight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "// ATLANTIC",
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = if (isLateNight) "MIDNIGHT SESSIONS" else "OFFLINE ARCHIVE",
                color = ChromeDark,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // AMOLED Toggle Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isNightMode) accentColor.copy(alpha = 0.2f) else Color(0x1AFFFFFF))
                    .border(
                        1.dp,
                        if (isNightMode) accentColor else Color(0x1AFFFFFF),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onToggleNightMode),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = "AMOLED Toggle",
                    tint = if (isNightMode) accentColor else ChromeMid,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Quick Customize Button (⚡)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenCustomize)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Customize",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "DECK ⚡",
                        color = ChromeHighlight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
