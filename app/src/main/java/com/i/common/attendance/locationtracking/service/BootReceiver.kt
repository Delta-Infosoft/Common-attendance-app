package com.i.common.attendance.locationtracking.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.utils.TrackingPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.jvm.java

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var prefs: TrackingPrefs

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Device may not be unlocked yet — CE storage (Keystore-backed
            // EncryptedSharedPreferences) is unavailable until then.
            if (!context.getSystemService(UserManager::class.java).isUserUnlocked) {
                Log.w("BootReceiver", "User locked, skipping start until unlock")
                return
            }
            if (prefs.isTracking()) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, LocationForegroundService::class.java)
                )
            }
        }
    }
}