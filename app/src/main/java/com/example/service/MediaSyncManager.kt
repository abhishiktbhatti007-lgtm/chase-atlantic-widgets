package com.example.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.example.receiver.CassettePlayerWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LiveMediaState(
    val title: String = "SWIM",
    val artist: String = "CHASE ATLANTIC",
    val album: String = "BEAUTY IN DEATH",
    val isPlaying: Boolean = false,
    val durationMs: Long = 228000L,
    val positionMs: Long = 0L,
    val albumArt: Bitmap? = null,
    val isLiveSynced: Boolean = false,
    val sourceApp: String = "Default"
)

object MediaSyncManager {

    private val _liveState = MutableStateFlow(LiveMediaState())
    val liveState: StateFlow<LiveMediaState> = _liveState.asStateFlow()

    private var activeController: MediaController? = null
    private var appContext: Context? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateFromMetadata(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateFromPlaybackState(state)
        }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        refreshActiveSessions()
    }

    fun isNotificationAccessGranted(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    fun openNotificationAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setActiveController(controller: MediaController?) {
        activeController?.unregisterCallback(controllerCallback)
        activeController = controller
        activeController?.registerCallback(controllerCallback)

        if (controller != null) {
            updateFromMetadata(controller.metadata)
            updateFromPlaybackState(controller.playbackState)
        }
    }

    fun refreshActiveSessions() {
        val context = appContext ?: return
        if (!isNotificationAccessGranted(context)) return

        try {
            val sessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
            val component = ComponentName(context, CassetteMediaListenerService::class.java)
            val controllers = sessionManager?.getActiveSessions(component)
            if (!controllers.isNullOrEmpty()) {
                // Prefer Spotify if available, otherwise first controller
                val spotifyController = controllers.firstOrNull {
                    it.packageName.contains("spotify", ignoreCase = true)
                } ?: controllers.first()

                setActiveController(spotifyController)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFromMetadata(metadata: MediaMetadata?) {
        if (metadata == null) return
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: return
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)

        val source = activeController?.packageName ?: "Spotify"
        val cleanSource = if (source.contains("spotify", ignoreCase = true)) "Spotify" else "Player"

        _liveState.update { current ->
            current.copy(
                title = title,
                artist = artist,
                album = album,
                durationMs = if (duration > 0) duration else current.durationMs,
                albumArt = art ?: current.albumArt,
                isLiveSynced = true,
                sourceApp = cleanSource
            )
        }
        notifyWidgets()
    }

    private fun updateFromPlaybackState(state: PlaybackState?) {
        if (state == null) return
        val isPlaying = state.state == PlaybackState.STATE_PLAYING
        val position = state.position

        _liveState.update { current ->
            current.copy(
                isPlaying = isPlaying,
                positionMs = position
            )
        }
        notifyWidgets()
    }

    // Called by Spotify Broadcast Receiver
    fun updateFromSpotifyBroadcast(
        track: String?,
        artist: String?,
        album: String?,
        lengthMs: Long,
        isPlaying: Boolean,
        positionMs: Long = 0L
    ) {
        if (track.isNullOrBlank()) return
        _liveState.update { current ->
            current.copy(
                title = track,
                artist = artist ?: current.artist,
                album = album ?: current.album,
                durationMs = if (lengthMs > 0) lengthMs else current.durationMs,
                isPlaying = isPlaying,
                positionMs = positionMs,
                isLiveSynced = true,
                sourceApp = "Spotify"
            )
        }
        notifyWidgets()
    }

    fun playPause() {
        val controller = activeController
        if (controller != null) {
            val state = controller.playbackState?.state
            if (state == PlaybackState.STATE_PLAYING) {
                controller.transportControls.pause()
            } else {
                controller.transportControls.play()
            }
        } else {
            // Toggle local fallback
            _liveState.update { it.copy(isPlaying = !it.isPlaying) }
            notifyWidgets()
        }
    }

    fun next() {
        val controller = activeController
        if (controller != null) {
            controller.transportControls.skipToNext()
        } else {
            notifyWidgets()
        }
    }

    fun previous() {
        val controller = activeController
        if (controller != null) {
            controller.transportControls.skipToPrevious()
        } else {
            notifyWidgets()
        }
    }

    fun seekTo(positionMs: Long) {
        activeController?.transportControls?.seekTo(positionMs)
        _liveState.update { it.copy(positionMs = positionMs) }
    }

    private fun notifyWidgets() {
        mainHandler.post {
            appContext?.let {
                CassettePlayerWidgetProvider.updateAll(it)
            }
        }
    }
}
