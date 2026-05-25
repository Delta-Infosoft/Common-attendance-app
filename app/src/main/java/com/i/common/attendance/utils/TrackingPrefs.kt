package com.i.common.attendance.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs =
        context.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE)

    fun setTracking(enabled: Boolean) {
        prefs.edit().putBoolean("tracking_enabled", enabled).apply()
    }

    fun isTracking(): Boolean =
        prefs.getBoolean("tracking_enabled", false)

    fun setTrackingDate(time: Long) {
        prefs.edit().putLong("tracking_start_time", time).apply()
    }

    fun getTrackingDate(): Long {
        return prefs.getLong("tracking_start_time", 0L)
    }

    fun clear() = prefs.edit().clear().apply()
}
