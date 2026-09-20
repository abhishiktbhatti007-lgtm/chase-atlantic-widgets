package com.example.receiver

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.ColorInt

object WidgetSettingsManager {
    private const val PREFS_NAME = "cassette_widget_prefs"

    const val KEY_ACCENT_COLOR = "widget_accent_color"
    const val KEY_ACCENT_NAME = "widget_accent_name"
    const val KEY_BG_STYLE = "widget_bg_style"
    const val KEY_SHOW_SECONDS = "widget_show_seconds"
    const val KEY_TIME_FORMAT_24H = "widget_time_format_24h"

    // Default accent: Crimson #FF1E56
    const val DEFAULT_ACCENT_COLOR = 0xFFFF1E56.toInt()
    const val DEFAULT_ACCENT_NAME = "Deep Crimson"

    // Background options:
    const val BG_OBSIDIAN_GLASS = 0
    const val BG_AMOLED_BLACK = 1
    const val BG_SMOKED_CARBON = 2

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @ColorInt
    fun getAccentColor(context: Context): Int {
        return getPrefs(context).getInt(KEY_ACCENT_COLOR, DEFAULT_ACCENT_COLOR)
    }

    fun getAccentName(context: Context): String {
        return getPrefs(context).getString(KEY_ACCENT_NAME, DEFAULT_ACCENT_NAME) ?: DEFAULT_ACCENT_NAME
    }

    fun setAccent(context: Context, @ColorInt color: Int, name: String) {
        getPrefs(context).edit()
            .putInt(KEY_ACCENT_COLOR, color)
            .putString(KEY_ACCENT_NAME, name)
            .apply()
        updateAllWidgets(context)
    }

    fun getBgStyle(context: Context): Int {
        return getPrefs(context).getInt(KEY_BG_STYLE, BG_OBSIDIAN_GLASS)
    }

    fun setBgStyle(context: Context, style: Int) {
        getPrefs(context).edit().putInt(KEY_BG_STYLE, style).apply()
        updateAllWidgets(context)
    }

    fun isShowSeconds(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SHOW_SECONDS, true)
    }

    fun setShowSeconds(context: Context, show: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_SECONDS, show).apply()
        updateAllWidgets(context)
    }

    fun is24Hour(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_TIME_FORMAT_24H, false)
    }

    fun set24Hour(context: Context, is24H: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_TIME_FORMAT_24H, is24H).apply()
        updateAllWidgets(context)
    }

    fun updateAllWidgets(context: Context) {
        CassetteClockWidgetProvider.updateAll(context)
        CassetteAnalog3DWidgetProvider.updateAll(context)
        Cassette3DWidgetProvider.updateAll(context)
        CassettePlayerWidgetProvider.updateAll(context)
        CassetteCountdownWidgetProvider.updateAll(context)
        CassetteHorizonWidgetProvider.updateAll(context)
        CassetteQuoteWidgetProvider.updateAll(context)
    }
}
