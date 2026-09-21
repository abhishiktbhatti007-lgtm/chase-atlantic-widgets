package com.example.service

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class CassetteMediaListenerService : NotificationListenerService() {

    private var sessionManager: MediaSessionManager? = null

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        handleActiveControllers(controllers)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        try {
            sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
            val component = ComponentName(this, CassetteMediaListenerService::class.java)
            sessionManager?.addOnActiveSessionsChangedListener(sessionListener, component)
            val initialSessions = sessionManager?.getActiveSessions(component)
            handleActiveControllers(initialSessions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        try {
            sessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // Whenever a music player updates its notification, refresh sessions
        if (sbn?.packageName?.contains("spotify", ignoreCase = true) == true ||
            sbn?.notification?.category == android.app.Notification.CATEGORY_TRANSPORT) {
            MediaSyncManager.refreshActiveSessions()
        }
    }

    private fun handleActiveControllers(controllers: List<MediaController>?) {
        if (!controllers.isNullOrEmpty()) {
            val spotify = controllers.firstOrNull {
                it.packageName.contains("spotify", ignoreCase = true)
            } ?: controllers.first()
            MediaSyncManager.setActiveController(spotify)
        }
    }
}
