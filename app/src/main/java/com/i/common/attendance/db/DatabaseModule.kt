package com.i.delta.attendanceappv2.db

import android.content.Context
import androidx.room.Room
import com.i.common.attendance.db.AppDatabase
import com.i.common.attendance.db.TrackedLocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "location_db"
        ).build()

    @Provides
    fun provideTrackedLocationDao(db: AppDatabase): TrackedLocationDao =
        db.trackedLocationDao()
}
