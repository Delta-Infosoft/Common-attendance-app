package com.i.common.attendance.locationtracking.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.i.common.attendance.R

object LocationNotification {

    const val CHANNEL_ID = "location_tracking_channel"
    const val NOTIFICATION_ID = 101

    fun create(context: Context): Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Attendance Tracking in Progress")
            .setContentText("Location tracking is being tracked in background. Please checkout to end tracking.")
            .setSmallIcon(R.drawable.ic_email)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }
}
