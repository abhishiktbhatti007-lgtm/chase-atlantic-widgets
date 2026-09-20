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

class CassetteClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_clock)

            try {
                // Dynamic Background
                val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                    WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                    WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                    else -> R.drawable.widget_glass_bg
                }
                views.setImageViewResource(R.id.widget_clock_bg, bgRes)

                // Dynamic Accent Color
                val accentColor = WidgetSettingsManager.getAccentColor(context)
                views.setTextColor(R.id.widget_clock_tag, accentColor)
                views.setTextColor(R.id.widget_clock_ampm, accentColor)

                // Dynamic Clock Format
                val is24H = WidgetSettingsManager.is24Hour(context)
                val showSec = WidgetSettingsManager.isShowSeconds(context)
                if (is24H) {
                    views.setCharSequence(R.id.widget_clock_time, "setFormat12Hour", "HH:mm")
                    views.setCharSequence(R.id.widget_clock_time, "setFormat24Hour", "HH:mm")
                    views.setViewVisibility(R.id.widget_clock_ampm, android.view.View.GONE)
                } else {
                    views.setCharSequence(R.id.widget_clock_time, "setFormat12Hour", "hh:mm")
                    views.setCharSequence(R.id.widget_clock_time, "setFormat24Hour", "hh:mm")
                    views.setViewVisibility(R.id.widget_clock_ampm, android.view.View.VISIBLE)
                }
                views.setViewVisibility(R.id.widget_clock_seconds, if (showSec) android.view.View.VISIBLE else android.view.View.GONE)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Open app on click
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_clock_root, pendingIntent)

            // Update tag based on current hour
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val tagText = if (hour in 0..5 || hour in 22..23) "LATE NIGHT" else "ATLANTIC DECK"
            views.setTextViewText(R.id.widget_clock_tag, tagText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassetteClockWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
