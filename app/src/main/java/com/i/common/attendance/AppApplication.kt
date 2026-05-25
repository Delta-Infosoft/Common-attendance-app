package com.i.common.attendance

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlin.jvm.java

@HiltAndroidApp
class AppApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        createLocationNotificationChannel()
    }

    private fun createLocationNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Used for attendance location tracking"

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}