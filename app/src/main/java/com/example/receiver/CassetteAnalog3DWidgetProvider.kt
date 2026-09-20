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

class CassetteAnalog3DWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.widget_analog_3d)

            try {
                // Dynamic Background
                val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                    WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                    WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                    else -> R.drawable.widget_glass_bg
                }
                views.setImageViewResource(R.id.widget_analog_bg, bgRes)

                // Dynamic Accent Color
                val accentColor = WidgetSettingsManager.getAccentColor(context)
                views.setTextColor(R.id.widget_analog_brand, accentColor)
                views.setTextColor(R.id.widget_analog_ampm, accentColor)

                // Dynamic Clock Format
                val is24H = WidgetSettingsManager.is24Hour(context)
                if (is24H) {
                    views.setCharSequence(R.id.widget_analog_time, "setFormat12Hour", "HH:mm")
                    views.setCharSequence(R.id.widget_analog_time, "setFormat24Hour", "HH:mm")
                    views.setViewVisibility(R.id.widget_analog_ampm, android.view.View.GONE)
                } else {
                    views.setCharSequence(R.id.widget_analog_time, "setFormat12Hour", "hh:mm")
                    views.setCharSequence(R.id.widget_analog_time, "setFormat24Hour", "hh:mm")
                    views.setViewVisibility(R.id.widget_analog_ampm, android.view.View.VISIBLE)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Click opens app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                10,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_analog_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassetteAnalog3DWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
