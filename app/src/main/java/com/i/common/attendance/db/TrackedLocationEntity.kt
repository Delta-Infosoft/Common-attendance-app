package com.i.common.attendance.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_locations")
data class TrackedLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mobileNo: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val insertedOn: String,
    val batteryStatus: String,
    val gpsStatus: String,
    val netStatus: String,
    val appVersion: String,
    val modelName: String,
    val androidVersion: String,
    val timestamp: Long
)
