package com.i.common.attendance.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.i.common.attendance.locationtracking.service.LocationForegroundService
import kotlin.jvm.java

class LocationWatchdogWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "LocationWatchdog"
    }

    override suspend fun doWork(): Result {

        val prefs = TrackingPrefs(applicationContext)
        Log.e(TAG, "⏰ WorkManager TRIGGERED at: ${System.currentTimeMillis()}")
        Log.e(TAG, "📌 Tracking status: ${prefs.isTracking()}")
        // ✅ Only run if user checked-in
        if (!prefs.isTracking()) {
            return Result.success()
        }

        try {
            val intent = Intent(applicationContext, LocationForegroundService::class.java)
            ContextCompat.startForegroundService(applicationContext, intent)

            Log.e(TAG, "🚀 Service START request sent")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to start service: ${e.message}")
            return Result.retry()
        }
        Log.e(TAG, "✅ WorkManager completed successfully")
        return Result.success()

        /*   // 🔍 Check if service is running
        if (!isServiceRunning()) {

        }*/
    }

    private fun isServiceRunning(): Boolean {
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager

        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationForegroundService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
}