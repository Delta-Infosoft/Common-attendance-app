package com.i.common.attendance.locationtracking.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
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
            if (prefs.isTracking()) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, LocationForegroundService::class.java)
                )
            }
        }
    }
}