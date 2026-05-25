package com.i.common.attendance.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TrackedLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedLocationDao(): TrackedLocationDao
}
