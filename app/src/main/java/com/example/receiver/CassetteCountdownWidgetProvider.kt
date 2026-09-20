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
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CassetteCountdownWidgetProvider : AppWidgetProvider() {

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
        if (intent.action == ACTION_CYCLE_COUNTDOWN) {
            currentDropIndex = (currentDropIndex + 1) % drops.size
            updateAll(context)
        }
    }

    companion object {
        const val ACTION_CYCLE_COUNTDOWN = "com.example.ACTION_WIDGET_CYCLE_COUNTDOWN"

        data class DropTarget(val title: String, val subtitle: String, val targetDaysOffset: Long)

        private val drops = listOf(
            DropTarget("LOST IN HEAVEN // TOUR", "OCTOBER 15 • WORLD TOUR", 24),
            DropTarget("MIDNIGHT DELUXE // ALBUM", "NOVEMBER 03 • NEW RECORD", 43),
            DropTarget("ATLANTIC CASSETTE // MERCH", "DECEMBER 01 • VAULT DROP", 71),
            DropTarget("NOIR LIVE SESSION // STREAM", "SEPTEMBER 28 • EXCLUSIVE", 7)
        )

        private var currentDropIndex = 0

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_countdown)
            val drop = drops[currentDropIndex]

            try {
                // Dynamic Background
                val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                    WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                    WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                    else -> R.drawable.widget_glass_bg
                }
                views.setImageViewResource(R.id.widget_countdown_bg, bgRes)

                // Dynamic Accent Color
                val accentColor = WidgetSettingsManager.getAccentColor(context)
                views.setTextColor(R.id.widget_countdown_tag, accentColor)
                views.setTextColor(R.id.widget_countdown_days, accentColor)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Drop Info
            views.setTextViewText(R.id.widget_countdown_title, drop.title)
            views.setTextViewText(R.id.widget_countdown_subtitle, drop.subtitle)

            // Simulated remaining numbers
            val days = drop.targetDaysOffset
            val cal = Calendar.getInstance()
            val hours = 23 - cal.get(Calendar.HOUR_OF_DAY)
            val mins = 59 - cal.get(Calendar.MINUTE)

            views.setTextViewText(R.id.widget_countdown_days, String.format("%02d", days))
            views.setTextViewText(R.id.widget_countdown_hours, String.format("%02d", hours))
            views.setTextViewText(R.id.widget_countdown_mins, String.format("%02d", mins))

            // Click root opens app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_countdown_root,
                PendingIntent.getActivity(
                    context,
                    30,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Cycle button
            val cycleIntent = Intent(context, CassetteCountdownWidgetProvider::class.java).apply {
                action = ACTION_CYCLE_COUNTDOWN
            }
            views.setOnClickPendingIntent(
                R.id.widget_countdown_cycle,
                PendingIntent.getBroadcast(
                    context,
                    31,
                    cycleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassetteCountdownWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
