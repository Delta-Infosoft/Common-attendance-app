package com.i.common.attendance.locationtracking.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.locationtracking.data.LocationRepository
import com.i.common.attendance.network.request.DeviceTrackingRequest
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.TAG_SERVICE
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.LocationWatchdogWorker
import com.i.common.attendance.utils.TrackingPrefs
import com.i.common.attendance.locationtracking.notification.LocationNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class LocationForegroundService : Service() {
    @Inject lateinit var fusedClient: FusedLocationProviderClient
    @Inject lateinit var repository: LocationRepository
    @Inject lateinit var sharedPref : EncryptedPrefHelper
    @Inject lateinit var prefs: TrackingPrefs
    private lateinit var locationCallback: LocationCallback
    private var isLocationStarted = false
    private var lastSentTime = 0L
    private var lastLocationTime = 0L
    private val LOCATION_TIMEOUT = 30 * 60 * 1000L // 30 minutes
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var serviceWakeLock: PowerManager.WakeLock? = null

   /* override fun onCreate() {
        super.onCreate()

        Log.e(TAG_SERVICE, "🚀 Service CREATED")

        startForeground(
            LocationNotification.NOTIFICATION_ID,
            LocationNotification.create(this)
        )
        acquireServiceWakeLock()
        //startLocationUpdates()
    }*/

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG_SERVICE, "🚀 Service CREATED")

        // ── CRITICAL FIX ──────────────────────────────────────────────
        // On Android 14+ (targetSdk 34+), startForeground() with a
        // manifest-declared foregroundServiceType="location" THROWS
        // SecurityException synchronously if:
        //   1) FOREGROUND_SERVICE_LOCATION isn't declared in the manifest, or
        //   2) neither ACCESS_FINE_LOCATION nor ACCESS_COARSE_LOCATION is
        //      currently granted (e.g. user revoked it), or
        //   3) the app isn't in an eligible state to launch a location FGS.
        // This previously crashed the entire app in onCreate(), before any
        // of onStartCommand()'s permission checks ever ran. We now gate the
        // call on a runtime permission check AND wrap it in try/catch as a
        // second line of defense — this makes the crash structurally
        // impossible, not just unlikely.
        if (!hasLocationPermission()) {
            Log.e(TAG_SERVICE, "🚫 No location permission at service creation — aborting safely")
            FirebaseCrashlytics.getInstance().log("LocationForegroundService: started without location permission — safe abort")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(applicationContext)
            // Safe to stopSelf() here WITHOUT calling startForeground():
            // this only crashes/ANRs if the service stays alive past the
            // ~5s promotion window. Stopping immediately in onCreate never
            // hits that window.
            stopSelf()
            return
        }

        try {
            startForeground(
                LocationNotification.NOTIFICATION_ID,
                LocationNotification.create(this)
            )
            acquireServiceWakeLock()
        } catch (e: SecurityException) {
            // Belt-and-suspenders: permission race, missing manifest
            // declaration, or OEM-specific enforcement slipped past our
            // check above.
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "🚨 startForeground SecurityException: ${e.message}")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(applicationContext)
            stopSelf()
        } catch (e: IllegalStateException) {
            // Covers ForegroundServiceStartNotAllowedException (API 31+):
            // app wasn't in an eligible state (background launch restriction).
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "🚨 startForeground not allowed (background restriction): ${e.message}")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(applicationContext)
            stopSelf()
        } catch (e: Exception) {
            // Last-resort catch-all: whatever it is, we must not crash.
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "🚨 startForeground unexpected error: ${e.message}")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(applicationContext)
            stopSelf()
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
    // ─── WakeLock acquire ───────────────────────────────────────
    private fun acquireServiceWakeLock() {
        try {
            if (serviceWakeLock?.isHeld == true) {
                Log.e(TAG_SERVICE, "⚠️ WakeLock already held")
                return
            }
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            serviceWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AttendanceApp::ServiceWakeLock"
            ).apply {
                setReferenceCounted(false)
                acquire(6 * 60 * 60 * 1000L)
            }
            Log.e(TAG_SERVICE, "🔋 Service WakeLock ACQUIRED")
        } catch (e: Exception) {
            Log.e(TAG_SERVICE, "WakeLock acquire failed: ${e.message}")
        }
    }

    // ─── WakeLock release ───────────────────────────────────────
    private fun releaseServiceWakeLock() {
        try {
            if (serviceWakeLock?.isHeld == true) {
                serviceWakeLock?.release()
                Log.e(TAG_SERVICE, "🔋 Service WakeLock RELEASED")
            }
        } catch (e: Exception) {
            Log.e(TAG_SERVICE, "WakeLock release failed: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!prefs.isTracking()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        // Guard: don't proceed if encrypted prefs aren't usable yet
        val user = try {
            sharedPref.getUser()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "sharedPref unavailable, stopping service: ${e.message}")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationUpdatesSafely()
            stopLocationWatchdog(applicationContext)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        } ?: run {
            Log.e(TAG_SERVICE, "No user session — stopping tracking service")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationUpdatesSafely()
            stopLocationWatchdog(applicationContext)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        acquireServiceWakeLock()
        requestEligibilityLocation()   // 🔥 REQUIRED
        checkLocationHealth()
        startLocationUpdates()

        return START_STICKY   // ✅ allowed because user explicitly checked in
    }
    private fun startLocationUpdates() {
        try {
            if (isLocationStarted) {
                Log.e(TAG_SERVICE, "⚠️ Location updates already running – skipping")
                return
            }

            isLocationStarted = true
            Log.e(TAG_SERVICE, "📡 Scheduling location updates (15–30 min)")

            // 🔹 Step B: long interval updates
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                15 * 60 * 1000L
            )
                .setMinUpdateIntervalMillis(15 * 60 * 1000L)
                .setMaxUpdateDelayMillis(30 * 60 * 1000L)
                .setWaitForAccurateLocation(false)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    if (isNextDay()) {
                        prefs.setTracking(false)
                        prefs.setTrackingDate(0L)
                        stopLocationUpdatesSafely()
                        stopLocationWatchdog(applicationContext)
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        return
                    }
                    val location = result.lastLocation ?: run {
                        Log.e(TAG_SERVICE, "⚠️ LocationResult is NULL")
                        return
                    }
                    lastLocationTime = System.currentTimeMillis()

                    Log.e(TAG_SERVICE, "📍 Location RECEIVED → lat=${location.latitude}, lng=${location.longitude}, acc=${location.accuracy}")

                    serviceScope.launch {
                        try {
                            sendLocationOnce(location)
                        } catch (e: Exception) {
                            Log.e(TAG_SERVICE, "sendLocationOnce crashed: ${e.message}")
                        }
                    }
                }
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        }catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "startLocationUpdates error: ${e.message}")
        }
    }
    private suspend fun sendLocationOnce(location: Location) {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "LocationApp::CallbackWakeLock"
        )
        try {
            // ✅ Acquire only for this operation (SAFE)
            wakeLock.acquire(2 * 60 * 1000L) // 2 minutes max

            val now = System.currentTimeMillis()

            if (now - lastSentTime < 60_000) { // 1 minute guard
                Log.e(TAG_SERVICE, "⚠️ Duplicate location ignored")
                return
            }

            lastSentTime = now

            val user = sharedPref.getUser()
            val request = DeviceTrackingRequest(
                mobileNo = user?.MobileNo ?: "",
                latitude = location.latitude,
                longitude = location.longitude,
                batteryStatus = Constants.getBatteryLevel(applicationContext).toString(),
                gpsStatus = Constants.isGpsEnabled(applicationContext).toString(),
                netStatus = Constants.isNetworkAvailable(applicationContext).toString(),
                appVersion = Constants.getAppVersion(applicationContext),
                insertedOn = Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss a"),
                modelName = Constants.getDeviceName(),
                androidVersion = Constants.getAndroidVersion()
            )

            val isInternet = Constants.isNetworkAvailable(applicationContext)
            if (isInternet) {
                val response = repository.insertLatLongAPI(request)
                if (response.isSuccessful) {
                    repository.syncPendingLocations()
                    Log.e(TAG_SERVICE, "💾 Location Api call at ${System.currentTimeMillis()}")
                } else {
                    repository.saveLocation(request)
                    Log.e(TAG_SERVICE, "💾 Location SAVED to DB at ${System.currentTimeMillis()}")
                }
            } else {
                repository.saveLocation(request)
                Log.e(TAG_SERVICE, "💾 Location SAVED to DB at ${System.currentTimeMillis()}")
            }
        }catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "Callback WakeLock acquire failed: ${e.message}")
        }finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.e(TAG_SERVICE, "🔋 WakeLock RELEASED")
            }
        }
    }
    private fun checkLocationHealth() {
        val now = System.currentTimeMillis()

        if (lastLocationTime == 0L) {
            Log.e(TAG_SERVICE, "⚠️ No location received yet")
            return
        }

        val diff = now - lastLocationTime

        if (diff > LOCATION_TIMEOUT) {
            Log.e(TAG_SERVICE, "🚨 Location stuck for ${diff / 60000} min → Restarting updates")

            try {
                if (::locationCallback.isInitialized) {
                    fusedClient.removeLocationUpdates(locationCallback)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e(TAG_SERVICE, "⚠️ Error removing updates: ${e.message}")
            }

            isLocationStarted = false
            startLocationUpdates()
        } else {
            Log.e(TAG_SERVICE, "✅ Location healthy (last update ${diff / 60000} min ago)")
        }
    }

    override fun onDestroy() {
        try {
            Log.e(TAG_SERVICE, "🛑 Service DESTROYED")

            if (::locationCallback.isInitialized) {
                fusedClient.removeLocationUpdates(locationCallback)
            }

            isLocationStarted = false
            serviceScope.cancel()
            stopForeground(STOP_FOREGROUND_REMOVE)
            // 🔋 Release WakeLock on destroy
            releaseServiceWakeLock()

            // 🔄 Schedule emergency restart if killed unexpectedly
            if (prefs.isTracking()) {
                val restartWork = androidx.work.OneTimeWorkRequestBuilder<LocationWatchdogWorker>()
                    .setInitialDelay(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                androidx.work.WorkManager.getInstance(applicationContext)
                    .enqueue(restartWork)
                Log.e(TAG_SERVICE, "🔄 Emergency restart worker scheduled")
            }
        }catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "Error in onDestroy: ${e.message}")
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun requestEligibilityLocation() {
        if (!hasLocationPermission()) {
            Log.e(TAG_SERVICE, "🚫 Permission revoked — stopping tracking")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationUpdatesSafely()
            stopLocationWatchdog(applicationContext)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        val isGpsOn = Constants.isGpsEnabled(applicationContext)

        try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (isNextDay()) {
                        Log.e(TAG_SERVICE, "🌙 Midnight crossed → stopping tracking")
                        prefs.setTracking(false)
                        prefs.setTrackingDate(0L)
                        stopLocationUpdatesSafely()
                        stopLocationWatchdog(applicationContext)
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        return@addOnSuccessListener
                    }
                    if (location != null) {
                        Log.e(TAG_SERVICE, "✅ Eligibility location received")
                        serviceScope.launch {
                            try {
                                sendLocationOnce(location)
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                                Log.e(TAG_SERVICE, "💥 sendLocationOnce crashed: ${e.message}")
                            }
                        }
                    } else {
                        Log.e(TAG_SERVICE, "⚠️ Eligibility location NULL")
                        if (!isGpsOn) {
                            Log.e(TAG_SERVICE, "🚫 GPS OFF → sending 0.0 location once")

                            val fakeLocation = Location("").apply {
                                latitude = 0.0
                                longitude = 0.0
                            }

                            serviceScope.launch {
                                try {
                                    sendLocationOnce(fakeLocation)
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                    Log.e(TAG_SERVICE, "💥 sendLocationOnce crashed: ${e.message}")
                                }
                            }
                        } else {
                            Log.e(TAG_SERVICE, "⚠️ Location NULL but GPS ON")
                        }
                    }
                }
                .addOnFailureListener { e -> Log.e(TAG_SERVICE, "getCurrentLocation failed: ${e.message}") }
        } catch (e: SecurityException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "Permission revoked mid-call: ${e.message}")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationUpdatesSafely()
            stopLocationWatchdog(applicationContext)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun isNextDay(): Boolean {
        val trackingStart = prefs.getTrackingDate()
        val now = System.currentTimeMillis()
        if (trackingStart == 0L) return false
        val startCal = Calendar.getInstance().apply { timeInMillis = trackingStart }
        val nowCal = Calendar.getInstance().apply { timeInMillis = now }

        return startCal.get(Calendar.DAY_OF_YEAR) != nowCal.get(Calendar.DAY_OF_YEAR) ||
                startCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)
    }
    private fun stopLocationWatchdog(context: Context) {
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork("location_watchdog")
    }
    private fun stopLocationUpdatesSafely() {
        try {
            if (::locationCallback.isInitialized) {
                fusedClient.removeLocationUpdates(locationCallback)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG_SERVICE, "Error stopping location updates: ${e.message}")
        }
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Avoid a pointless restart-into-safe-abort cycle: if permission is
        // already known to be missing, don't relaunch the FGS at all — just
        // let tracking stay off. onCreate() would abort safely either way,
        // but skipping the relaunch avoids waking the process for nothing.
        if (prefs.isTracking() && hasLocationPermission()) {
            val restartIntent = Intent(applicationContext, LocationForegroundService::class.java)
            startForegroundService(restartIntent)
        } else if (prefs.isTracking()) {
            Log.e(TAG_SERVICE, "🚫 Task removed, no location permission — not restarting")
            prefs.setTracking(false)
            prefs.setTrackingDate(0L)
            stopLocationWatchdog(applicationContext)
        }
        super.onTaskRemoved(rootIntent)
    }
    /*override fun onTaskRemoved(rootIntent: Intent?) {
        if (prefs.isTracking()) {
            val restartIntent = Intent(applicationContext, LocationForegroundService::class.java)
            startForegroundService(restartIntent)
        }
        super.onTaskRemoved(rootIntent)
    }*/

}





