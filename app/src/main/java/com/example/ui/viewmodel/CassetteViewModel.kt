package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WidgetSettingsEntity
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.random.Random

data class TimeState(
    val hour12: String = "02",
    val hour24: String = "02",
    val minute: String = "47",
    val second: String = "33",
    val amPm: String = "AM",
    val dayOfWeek: String = "FRIDAY",
    val formattedDate: String = "OCTOBER 24",
    val dayOfMonth: Int = 24,
    val monthName: String = "OCT",
    val moonPhase: String = "WANING CRESCENT 82%",
    val hoursToSunrise: String = "3H 45M",
    val isLateNight: Boolean = true
)

data class CountdownRemaining(
    val days: Long = 0,
    val hours: Long = 18,
    val minutes: Long = 42,
    val seconds: Long = 10,
    val isExpired: Boolean = false
)

data class CassetteUiState(
    val timeState: TimeState = TimeState(),
    val currentTrack: Track,
    val isPlaying: Boolean = true,
    val currentPositionMs: Long = 14200,
    val isFavoriteSong: Boolean = true,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = true,
    val currentLyricIndex: Int = 3,
    val visualizerLevels: List<Float> = List(20) { 0.4f },
    val waveformPoints: List<Float> = List(30) { 0.5f },
    val enabledWidgets: Set<WidgetType> = WidgetType.values().toSet(),
    val clockStyle: ClockStyle = ClockStyle.OVERSIZED_DIGITAL,
    val themeAccent: ThemeAccent = ThemeAccent.CRIMSON,
    val ambientMode: AmbientMode = AmbientMode.SMOKE_GLASS,
    val isNightModeOnly: Boolean = false,
    val countdownTitle: String = "MIDNIGHT ALBUM DROP",
    val countdownTargetEpoch: Long = System.currentTimeMillis() + (18 * 3600 * 1000L) + (42 * 60 * 1000L),
    val countdownRemaining: CountdownRemaining = CountdownRemaining(),
    val currentQuote: QuoteItem,
    val activeSoundscapeId: String? = "s1",
    val isSoundscapePlaying: Boolean = true,
    val soundscapeVolume: Float = 0.65f,
    val showCustomizerSheet: Boolean = false,
    val isCopiedNotification: Boolean = false
)

class CassetteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(
        CassetteUiState(
            currentTrack = repository.sampleTracks.first(),
            currentQuote = repository.moodQuotes.first()
        )
    )
    val uiState: StateFlow<CassetteUiState> = _uiState.asStateFlow()

    val allTracks: List<Track> = repository.sampleTracks
    val allQuotes: List<QuoteItem> = repository.moodQuotes
    val allSoundscapes: List<SoundscapeItem> = repository.soundscapes

    init {
        loadSavedSettings()
        startTimeTicker()
        startPlaybackTicker()
        startVisualizerEngine()
    }

    private fun loadSavedSettings() {
        viewModelScope.launch {
            repository.getSettings().collect { entity ->
                entity?.let { settings ->
                    val savedWidgets = settings.enabledWidgets.split(",")
                        .mapNotNull { name ->
                            try { WidgetType.valueOf(name.trim()) } catch (_: Exception) { null }
                        }.toSet()

                    val clock = try { ClockStyle.valueOf(settings.clockStyle) } catch (_: Exception) { ClockStyle.OVERSIZED_DIGITAL }
                    val theme = try { ThemeAccent.valueOf(settings.themeAccent) } catch (_: Exception) { ThemeAccent.CRIMSON }
                    val ambient = try { AmbientMode.valueOf(settings.ambientMode) } catch (_: Exception) { AmbientMode.SMOKE_GLASS }

                    _uiState.update { current ->
                        current.copy(
                            enabledWidgets = if (savedWidgets.isNotEmpty()) savedWidgets else current.enabledWidgets,
                            clockStyle = clock,
                            themeAccent = theme,
                            ambientMode = ambient,
                            countdownTitle = settings.countdownTitle,
                            countdownTargetEpoch = settings.countdownTargetEpoch
                        )
                    }
                }
            }
        }
    }

    private fun persistSettings() {
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveSettings(
                WidgetSettingsEntity(
                    id = 1,
                    enabledWidgets = s.enabledWidgets.joinToString(",") { it.name },
                    clockStyle = s.clockStyle.name,
                    themeAccent = s.themeAccent.name,
                    ambientMode = s.ambientMode.name,
                    countdownTitle = s.countdownTitle,
                    countdownTargetEpoch = s.countdownTargetEpoch
                )
            )
        }
    }

    private fun startTimeTicker() {
        viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val hour12 = SimpleDateFormat("hh", Locale.getDefault()).format(now.time)
                val hour24 = SimpleDateFormat("HH", Locale.getDefault()).format(now.time)
                val minute = SimpleDateFormat("mm", Locale.getDefault()).format(now.time)
                val second = SimpleDateFormat("ss", Locale.getDefault()).format(now.time)
                val amPm = SimpleDateFormat("a", Locale.getDefault()).format(now.time).uppercase()
                val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(now.time).uppercase()
                val formattedDate = SimpleDateFormat("MMMM d", Locale.getDefault()).format(now.time).uppercase()
                val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(now.time).uppercase()

                val hourInt = now.get(Calendar.HOUR_OF_DAY)
                val isLateNight = hourInt in 0..5 || hourInt >= 22
                val hoursToSunrise = if (hourInt < 6) "${6 - hourInt}H ${60 - now.get(Calendar.MINUTE)}M" else "NIGHTFALL IN ${22 - hourInt}H"

                // Calculate countdown
                val diffMs = _uiState.value.countdownTargetEpoch - System.currentTimeMillis()
                val remaining = if (diffMs > 0) {
                    val days = diffMs / (1000 * 60 * 60 * 24)
                    val hours = (diffMs / (1000 * 60 * 60)) % 24
                    val mins = (diffMs / (1000 * 60)) % 60
                    val secs = (diffMs / 1000) % 60
                    CountdownRemaining(days, hours, mins, secs, false)
                } else {
                    CountdownRemaining(0, 0, 0, 0, true)
                }

                _uiState.update { current ->
                    current.copy(
                        timeState = TimeState(
                            hour12 = hour12,
                            hour24 = hour24,
                            minute = minute,
                            second = second,
                            amPm = amPm,
                            dayOfWeek = dayOfWeek,
                            formattedDate = formattedDate,
                            dayOfMonth = dayOfMonth,
                            monthName = monthName,
                            moonPhase = "WANING CRESCENT 82%",
                            hoursToSunrise = hoursToSunrise,
                            isLateNight = isLateNight
                        ),
                        countdownRemaining = remaining
                    )
                }
                delay(1000)
            }
        }
    }

    private fun startPlaybackTicker() {
        viewModelScope.launch {
            while (true) {
                if (_uiState.value.isPlaying) {
                    _uiState.update { current ->
                        val track = current.currentTrack
                        val durationMs = track.durationSeconds * 1000L
                        val newPos = (current.currentPositionMs + 500L)
                        val clampedPos = if (newPos >= durationMs) {
                            if (current.isRepeat) 0L else durationMs
                        } else newPos

                        // Find current active lyric index
                        var activeLyric = 0
                        for (i in track.lyrics.indices) {
                            if (clampedPos >= track.lyrics[i].timestampMs) {
                                activeLyric = i
                            }
                        }

                        current.copy(
                            currentPositionMs = clampedPos,
                            currentLyricIndex = activeLyric
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun startVisualizerEngine() {
        viewModelScope.launch {
            var phase = 0f
            while (true) {
                phase += 0.25f
                val playing = _uiState.value.isPlaying
                val numBars = 20
                val newLevels = List(numBars) { i ->
                    if (playing) {
                        val base = (sin(phase + i * 0.45) + 1.0) / 2.0
                        val jitter = Random.nextFloat() * 0.45f
                        ((base.toFloat() * 0.6f) + jitter).coerceIn(0.12f, 1.0f)
                    } else {
                        // Subtle idle pulse
                        ((sin(phase * 0.4 + i * 0.2) + 1.0) * 0.08 + 0.05).toFloat().coerceIn(0.04f, 0.22f)
                    }
                }

                val numWavePoints = 30
                val newWave = List(numWavePoints) { i ->
                    val freq = if (playing) 1.0 else 0.3
                    val amp = if (playing) 0.4f else 0.12f
                    (sin(phase * freq + i * 0.3) * amp + 0.5f).toFloat().coerceIn(0.05f, 0.95f)
                }

                _uiState.update { it.copy(visualizerLevels = newLevels, waveformPoints = newWave) }
                delay(80)
            }
        }
    }

    fun togglePlayPause() {
        triggerHaptic()
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun nextTrack() {
        triggerHaptic()
        val currentIndex = allTracks.indexOfFirst { it.id == _uiState.value.currentTrack.id }
        val nextIndex = if (_uiState.value.isShuffle) {
            (allTracks.indices.filter { it != currentIndex }.randomOrNull() ?: 0)
        } else {
            (currentIndex + 1) % allTracks.size
        }
        val next = allTracks[nextIndex]
        _uiState.update {
            it.copy(
                currentTrack = next,
                currentPositionMs = 0L,
                currentLyricIndex = 0
            )
        }
    }

    fun previousTrack() {
        triggerHaptic()
        val currentIndex = allTracks.indexOfFirst { it.id == _uiState.value.currentTrack.id }
        val prevIndex = if (currentIndex <= 0) allTracks.size - 1 else currentIndex - 1
        val prev = allTracks[prevIndex]
        _uiState.update {
            it.copy(
                currentTrack = prev,
                currentPositionMs = 0L,
                currentLyricIndex = 0
            )
        }
    }

    fun selectTrack(track: Track) {
        triggerHaptic()
        _uiState.update {
            it.copy(
                currentTrack = track,
                currentPositionMs = 0L,
                currentLyricIndex = 0,
                isPlaying = true
            )
        }
    }

    fun seekToFraction(fraction: Float) {
        val totalMs = _uiState.value.currentTrack.durationSeconds * 1000L
        val targetMs = (fraction * totalMs).toLong().coerceIn(0L, totalMs)
        _uiState.update { it.copy(currentPositionMs = targetMs) }
    }

    fun jumpToLyric(index: Int) {
        triggerHaptic()
        val track = _uiState.value.currentTrack
        if (index in track.lyrics.indices) {
            val targetMs = track.lyrics[index].timestampMs
            _uiState.update { it.copy(currentPositionMs = targetMs, currentLyricIndex = index) }
        }
    }

    fun toggleFavoriteSong() {
        triggerHaptic()
        _uiState.update { it.copy(isFavoriteSong = !it.isFavoriteSong) }
    }

    fun toggleShuffle() {
        triggerHaptic()
        _uiState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun toggleRepeat() {
        triggerHaptic()
        _uiState.update { it.copy(isRepeat = !it.isRepeat) }
    }

    fun setClockStyle(style: ClockStyle) {
        triggerHaptic()
        _uiState.update { it.copy(clockStyle = style) }
        persistSettings()
    }

    fun setThemeAccent(accent: ThemeAccent) {
        triggerHaptic()
        _uiState.update { it.copy(themeAccent = accent) }
        persistSettings()
    }

    fun setAmbientMode(mode: AmbientMode) {
        triggerHaptic()
        _uiState.update { it.copy(ambientMode = mode) }
        persistSettings()
    }

    fun toggleNightMode() {
        triggerHaptic()
        _uiState.update { it.copy(isNightModeOnly = !it.isNightModeOnly) }
    }

    fun toggleWidget(type: WidgetType) {
        triggerHaptic()
        _uiState.update { current ->
            val set = current.enabledWidgets.toMutableSet()
            if (set.contains(type)) {
                if (set.size > 1) set.remove(type)
            } else {
                set.add(type)
            }
            current.copy(enabledWidgets = set)
        }
        persistSettings()
    }

    fun shuffleQuote() {
        triggerHaptic()
        val current = _uiState.value.currentQuote
        val next = allQuotes.filter { it.id != current.id }.randomOrNull() ?: current
        _uiState.update { it.copy(currentQuote = next) }
    }

    fun copyQuoteToClipboard(quote: QuoteItem) {
        triggerHaptic()
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Chase Atlantic Quote", "\"${quote.quote}\" — ${quote.source}")
        clipboard.setPrimaryClip(clip)

        viewModelScope.launch {
            repository.saveFavoriteQuote(quote)
            _uiState.update { it.copy(isCopiedNotification = true) }
            delay(2000)
            _uiState.update { it.copy(isCopiedNotification = false) }
        }
    }

    fun toggleSoundscape(soundscapeId: String) {
        triggerHaptic()
        _uiState.update { current ->
            if (current.activeSoundscapeId == soundscapeId) {
                current.copy(isSoundscapePlaying = !current.isSoundscapePlaying)
            } else {
                current.copy(activeSoundscapeId = soundscapeId, isSoundscapePlaying = true)
            }
        }
    }

    fun setSoundscapeVolume(vol: Float) {
        _uiState.update { it.copy(soundscapeVolume = vol.coerceIn(0f, 1f)) }
    }

    fun setCustomizerSheetVisible(visible: Boolean) {
        triggerHaptic()
        _uiState.update { it.copy(showCustomizerSheet = visible) }
    }

    fun setCountdown(title: String, targetEpoch: Long) {
        _uiState.update { it.copy(countdownTitle = title, countdownTargetEpoch = targetEpoch) }
        persistSettings()
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(18)
            }
        } catch (_: Exception) {}
    }
}
