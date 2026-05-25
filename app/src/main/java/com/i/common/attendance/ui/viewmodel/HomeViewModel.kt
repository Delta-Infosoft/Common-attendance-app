package com.i.common.attendance.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.i.common.attendance.locationtracking.data.LocationRepository
import com.i.common.attendance.locationtracking.service.LocationForegroundService
import com.i.common.attendance.utils.Constants.isServiceRunning
import com.i.common.attendance.utils.LocationWatchdogWorker
import com.i.common.attendance.utils.TrackingPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: TrackingPrefs,
    private val app: Application,
    private val repository: LocationRepository
) : ViewModel() {

    fun startTracking() {

        Log.e("HomeVM", "▶️ START tracking requested")

        val serviceRunning = isServiceRunning(
            app,
            LocationForegroundService::class.java
        )

        if (serviceRunning) {
            Log.e("HomeVM", "⚠️ Service already running")
            prefs.setTracking(true)
            prefs.setTrackingDate(System.currentTimeMillis())
            startLocationWatchdog(app)
            return
        }

        Log.e("HomeVM", "🚀 Starting Foreground Service")

        prefs.setTracking(true)
        prefs.setTrackingDate(System.currentTimeMillis())
        ContextCompat.startForegroundService(app, Intent(app, LocationForegroundService::class.java))
        startLocationWatchdog(app)
    }

    fun stopTracking() {
        Log.e("HomeVM", "⏹ STOP tracking requested")
        val isRunning = isServiceRunning(
            app,
            LocationForegroundService::class.java
        )

        if (!isRunning) {
            Log.e("HomeVM", "⚠️ Service already stopped")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(app)
            return
        }

        prefs.setTracking(false)
        prefs.setTrackingDate(0L)
        app.stopService(Intent(app, LocationForegroundService::class.java))
        stopLocationWatchdog(app)
    }

    fun clearLocalData() {
        Log.e("HomeVM", "⏹ STOP tracking requested")
        val isRunning = isServiceRunning(app, LocationForegroundService::class.java)

        if (!isRunning) {
            Log.e("HomeVM", "⚠️ Service already stopped")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            prefs.clear()
            stopLocationWatchdog(app)
            return
        }

        prefs.setTracking(false)
        prefs.setTrackingDate(0L)
        app.stopService(Intent(app, LocationForegroundService::class.java))

        // 🧹 Clear encrypted prefs
        prefs.clear()
        stopLocationWatchdog(app)
    }
    private fun startLocationWatchdog(context: Context) {

        val workRequest =
            androidx.work.PeriodicWorkRequestBuilder<LocationWatchdogWorker>(
                15, java.util.concurrent.TimeUnit.MINUTES
            ).build()

        androidx.work.WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "location_watchdog",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    fun stopLocationWatchdog(context: Context) {
        androidx.work.WorkManager.getInstance(context)
            .cancelUniqueWork("location_watchdog")
    }

}
