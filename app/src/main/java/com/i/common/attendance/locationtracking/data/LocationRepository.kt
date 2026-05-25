package com.i.common.attendance.locationtracking.data

import android.util.Log
import com.i.common.attendance.network.request.DeviceTrackingRequest
import com.i.common.attendance.network.response.LocationTrackingResponse
import com.i.common.attendance.network.service.ApiService
import com.i.common.attendance.utils.Constants.TAG_REPO
import com.i.common.attendance.db.TrackedLocationDao
import com.i.common.attendance.db.TrackedLocationEntity
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val dao: TrackedLocationDao,
    @Named("DEFAULT")private val apiService: ApiService
) {

    suspend fun insertLatLongAPI(request: DeviceTrackingRequest): Response<LocationTrackingResponse> {
        return apiService.insertLatLongAPI(request.toMultipartBody())
    }

    suspend fun saveLocation(request: DeviceTrackingRequest) {
        Log.e("LOCATION_REPO", "💾 Saving location OFFLINE")

        dao.insert(
            TrackedLocationEntity(
                mobileNo = request.mobileNo,
                latitude = request.latitude,
                longitude = request.longitude,
                accuracy = 0f,

                insertedOn = request.insertedOn,
                batteryStatus = request.batteryStatus,
                gpsStatus = request.gpsStatus,
                netStatus = request.netStatus,
                appVersion = request.appVersion,
                modelName = request.modelName,
                androidVersion = request.androidVersion,

                timestamp = System.currentTimeMillis()
            )
        )

        Log.e(TAG_REPO, "✅ DB INSERT successful")
    }

    suspend fun syncPendingLocations() {
        val pendingList = dao.getAllLocationsList()

        if (pendingList.isEmpty()) return

        pendingList.forEach { item ->
            try {
                val request = DeviceTrackingRequest(
                    mobileNo = item.mobileNo,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    insertedOn = item.insertedOn,
                    batteryStatus = item.batteryStatus,
                    gpsStatus = item.gpsStatus,
                    netStatus = item.netStatus,
                    appVersion = item.appVersion,
                    modelName = item.modelName,
                    androidVersion = item.androidVersion
                )

                insertLatLongAPI(request)
            } catch (e: Exception) {
                Log.e("SYNC", "❌ Failed → will retry later")
                return
            }
        }

        dao.deleteAll()
    }
}

