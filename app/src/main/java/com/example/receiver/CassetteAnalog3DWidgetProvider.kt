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

            // Dynamic Background
            val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                else -> R.drawable.widget_glass_bg
            }
            views.setInt(R.id.widget_analog_root, "setBackgroundResource", bgRes)

            // Dynamic Accent Color
            val accentColor = WidgetSettingsManager.getAccentColor(context)
            views.setTextColor(R.id.widget_analog_brand, accentColor)
            views.setTextColor(R.id.widget_analog_ampm, accentColor)

            // Dynamic Clock Format
            val is24H = WidgetSettingsManager.is24Hour(context)
            views.setString(R.id.widget_analog_time, "setFormat12Hour", if (is24H) "HH:mm" else "hh:mm")
            views.setString(R.id.widget_analog_time, "setFormat24Hour", if (is24H) "HH:mm" else "hh:mm")
            views.setString(R.id.widget_analog_ampm, "setFormat12Hour", if (is24H) " " else "a")
            views.setString(R.id.widget_analog_ampm, "setFormat24Hour", " ")

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
