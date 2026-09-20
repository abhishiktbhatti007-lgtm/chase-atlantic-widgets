package com.example.receiver

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build

object WidgetPinHelper {

    fun isPinSupported(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.isRequestPinAppWidgetSupported
        } else {
            false
        }
    }

    fun pinClockWidget(context: Context): Boolean {
        return requestPin(context, CassetteClockWidgetProvider::class.java)
    }

    fun pinPlayerWidget(context: Context): Boolean {
        return requestPin(context, CassettePlayerWidgetProvider::class.java)
    }

    fun pinQuoteWidget(context: Context): Boolean {
        return requestPin(context, CassetteQuoteWidgetProvider::class.java)
    }

    private fun requestPin(context: Context, providerClass: Class<*>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val myProvider = ComponentName(context, providerClass)
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    providerClass.hashCode(),
                    Intent("com.example.WIDGET_PINNED"),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                return appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
            }
        }
        return false
    }
}
