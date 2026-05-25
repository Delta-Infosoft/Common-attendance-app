package com.i.common.attendance.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackedLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: TrackedLocationEntity)

    // ✅ Get ALL for sync (IMPORTANT → NOT Flow)
    @Query("SELECT * FROM tracked_locations ORDER BY timestamp ASC")
    suspend fun getAllLocationsList(): List<TrackedLocationEntity>

    // ✅ Delete all after sync
    @Query("DELETE FROM tracked_locations")
    suspend fun deleteAll()
}
