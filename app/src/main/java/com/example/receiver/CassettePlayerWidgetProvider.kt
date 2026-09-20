package com.example.receiver

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class CassettePlayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                isPlaying = !isPlaying
                updateAll(context)
            }
            ACTION_NEXT -> {
                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                isPlaying = true
                updateAll(context)
            }
            ACTION_PREV -> {
                currentTrackIndex = if (currentTrackIndex - 1 < 0) tracks.size - 1 else currentTrackIndex - 1
                isPlaying = true
                updateAll(context)
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.ACTION_WIDGET_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.ACTION_WIDGET_NEXT"
        const val ACTION_PREV = "com.example.ACTION_WIDGET_PREV"

        data class WidgetTrack(val title: String, val artist: String, val coverRes: Int)

        private val tracks = listOf(
            WidgetTrack("SWIM", "CHASE ATLANTIC", R.drawable.img_album_beauty),
            WidgetTrack("INTO IT", "CHASE ATLANTIC", R.drawable.img_album_phases),
            WidgetTrack("SLOW DOWN", "CHASE ATLANTIC", R.drawable.img_album_beauty),
            WidgetTrack("MEDDLE ABOUT", "CHASE ATLANTIC", R.drawable.img_album_phases),
            WidgetTrack("CONSUME", "CHASE ATLANTIC", R.drawable.img_album_beauty)
        )

        private var currentTrackIndex = 0
        private var isPlaying = true

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_player)
            val track = tracks[currentTrackIndex]

            try {
                // Dynamic Background
                val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                    WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                    WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                    else -> R.drawable.widget_glass_bg
                }
                views.setImageViewResource(R.id.widget_player_bg, bgRes)

                views.setTextViewText(R.id.widget_player_title, track.title)
                views.setTextViewText(R.id.widget_player_artist, track.artist)
                views.setImageViewResource(R.id.widget_player_cover, R.drawable.ic_album_art)

                // Play/Pause icon
                views.setImageViewResource(
                    R.id.widget_player_play_pause,
                    if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Click root opens MainActivity
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_player_root,
                PendingIntent.getActivity(
                    context,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Control pending intents
            val prevIntent = Intent(context, CassettePlayerWidgetProvider::class.java).apply {
                action = ACTION_PREV
            }
            views.setOnClickPendingIntent(
                R.id.widget_player_prev,
                PendingIntent.getBroadcast(
                    context,
                    1,
                    prevIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            val playIntent = Intent(context, CassettePlayerWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            views.setOnClickPendingIntent(
                R.id.widget_player_play_pause,
                PendingIntent.getBroadcast(
                    context,
                    2,
                    playIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            val nextIntent = Intent(context, CassettePlayerWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            views.setOnClickPendingIntent(
                R.id.widget_player_next,
                PendingIntent.getBroadcast(
                    context,
                    3,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassettePlayerWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
