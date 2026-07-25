package com.i.common.attendance.securepref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EncryptedPrefModule {

    private const val PREF_NAME = "secure_shared_pre_delta_attendance_app"
    private const val TAG = "EncryptedPrefModule"

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKey: MasterKey
    ): SharedPreferences {
        return try {
            createEncryptedPrefs(context, masterKey)
        } catch (e: Exception) {
            // Covers AEADBadTagException, GeneralSecurityException, IOException.
            // Ciphertext/key mismatch = data is unrecoverable, not corrupted logic.
            Log.w(TAG, "EncryptedSharedPreferences unreadable, resetting store", e)
            context.deleteSharedPreferences(PREF_NAME)
            try {
                createEncryptedPrefs(context, masterKey)
            } catch (e2: Exception) {
                // Extremely rare second failure (e.g. Keystore itself unavailable).
                // Fall back to a plain, non-crashing empty store rather than
                // taking down app launch entirely.
                Log.e(TAG, "Recovery attempt failed, using fallback prefs", e2)
                context.getSharedPreferences("${PREF_NAME}_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    private fun createEncryptedPrefs(context: Context, masterKey: MasterKey): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
