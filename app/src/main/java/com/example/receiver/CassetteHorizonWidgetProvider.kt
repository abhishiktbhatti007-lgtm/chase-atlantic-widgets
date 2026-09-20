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

class CassetteHorizonWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.widget_horizon)

            // Dynamic Background
            val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                else -> R.drawable.widget_glass_bg
            }
            views.setInt(R.id.widget_horizon_root, "setBackgroundResource", bgRes)

            // Dynamic Accent Color
            val accentColor = WidgetSettingsManager.getAccentColor(context)
            views.setTextColor(R.id.widget_horizon_header, accentColor)
            views.setTextColor(R.id.widget_horizon_badge, accentColor)

            // Calculate hours until sunrise (assumed 6:00 AM)
            val cal = Calendar.getInstance()
            val currentHour = cal.get(Calendar.HOUR_OF_DAY)
            val currentMin = cal.get(Calendar.MINUTE)

            val hoursUntilSunrise = if (currentHour < 6) {
                5 - currentHour
            } else {
                24 + 5 - currentHour
            }
            val minsUntilSunrise = 60 - currentMin

            val sunriseStr = "${hoursUntilSunrise}H ${minsUntilSunrise}M UNTIL SUNRISE"
            views.setTextViewText(R.id.widget_horizon_sunrise_text, sunriseStr)

            // Moon phase by day of month
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val (moonName, illumination) = when (day % 8) {
                0 -> "NEW MOON" to "0%"
                1 -> "WAXING CRESCENT" to "23%"
                2 -> "FIRST QUARTER" to "50%"
                3 -> "WAXING GIBBOUS" to "78%"
                4 -> "FULL MOON" to "100%"
                5 -> "WANING GIBBOUS" to "82%"
                6 -> "LAST QUARTER" to "50%"
                else -> "WANING CRESCENT" to "19%"
            }
            views.setTextViewText(R.id.widget_horizon_moon_title, "$moonName $illumination")

            // Badge text
            val badge = if (currentHour in 0..5) "DEEP NIGHT" else if (currentHour in 20..23) "LATE NOIR" else "DAY HORIZON"
            views.setTextViewText(R.id.widget_horizon_badge, badge)

            // Click root opens app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_horizon_root,
                PendingIntent.getActivity(
                    context,
                    40,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassetteHorizonWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
