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

class CassetteQuoteWidgetProvider : AppWidgetProvider() {

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
        if (intent.action == ACTION_SHUFFLE_QUOTE) {
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
            updateAll(context)
        }
    }

    companion object {
        const val ACTION_SHUFFLE_QUOTE = "com.example.ACTION_WIDGET_SHUFFLE_QUOTE"

        data class WidgetQuote(val text: String, val tag: String, val source: String)

        private val quotes = listOf(
            WidgetQuote("“Too late to sleep, too early to think.”", "● 3:14 AM // MOOD", "CHASE ATLANTIC // BEAUTY IN DEATH"),
            WidgetQuote("“Lost in the smoke, caught between midnight and forever.”", "● MIDNIGHT NOIR", "CHASE ATLANTIC // PHASES"),
            WidgetQuote("“Bass rattling the glass at 4 in the morning.”", "● 808 DISTORTION", "CHASE ATLANTIC // LOST IN CALIFORNIA"),
            WidgetQuote("“Black sunglasses in a dim-lit hotel lobby.”", "● LIQUID CHROME", "CHASE ATLANTIC // BEAUTY IN DEATH"),
            WidgetQuote("“Feel the pulse of the city through the stereo.”", "● NEON ECHO", "CHASE ATLANTIC // PHASES")
        )

        private var currentQuoteIndex = 0

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quote)
            val quote = quotes[currentQuoteIndex]

            try {
                // Dynamic Background
                val bgRes = when (WidgetSettingsManager.getBgStyle(context)) {
                    WidgetSettingsManager.BG_AMOLED_BLACK -> R.drawable.widget_bg_amoled
                    WidgetSettingsManager.BG_SMOKED_CARBON -> R.drawable.widget_bg_carbon
                    else -> R.drawable.widget_glass_bg
                }
                views.setImageViewResource(R.id.widget_quote_bg, bgRes)

                // Dynamic Accent Color
                val accentColor = WidgetSettingsManager.getAccentColor(context)
                views.setTextColor(R.id.widget_quote_tag, accentColor)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            views.setTextViewText(R.id.widget_quote_text, quote.text)
            views.setTextViewText(R.id.widget_quote_tag, quote.tag)
            views.setTextViewText(R.id.widget_quote_source, quote.source)

            // Click root opens MainActivity
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_quote_root,
                PendingIntent.getActivity(
                    context,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Shuffle button pending intent
            val shuffleIntent = Intent(context, CassetteQuoteWidgetProvider::class.java).apply {
                action = ACTION_SHUFFLE_QUOTE
            }
            views.setOnClickPendingIntent(
                R.id.widget_quote_refresh,
                PendingIntent.getBroadcast(
                    context,
                    1,
                    shuffleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CassetteQuoteWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
