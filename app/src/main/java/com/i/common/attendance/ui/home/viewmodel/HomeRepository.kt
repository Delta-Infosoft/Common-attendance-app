package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.request.CheckInCheckOutRequest
import com.i.common.attendance.network.request.DeviceTrackingRequest
import com.i.common.attendance.network.request.GetAttendanceInOutRequest
import com.i.common.attendance.network.request.GetRecordsRequest
import com.i.common.attendance.network.request.LogoutRequest
import com.i.common.attendance.network.request.TextListRequest
import com.i.common.attendance.network.response.AttendanceRecordResponse
import com.i.common.attendance.network.response.GetRecords
import com.i.common.attendance.network.response.LocationTrackingResponse
import com.i.common.attendance.network.response.LogoutResponse
import com.i.common.attendance.network.response.Status
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class HomeRepository @Inject constructor( @Named("DEFAULT")private val apiService: ApiService) {

    suspend fun getRecordAPI(request: GetRecordsRequest): Response<GetRecords> {
        return apiService.getRecordAPI(request.toMultipartBody())
    }

    suspend fun getCheckInCheckOutStatusAPI(request: CheckInCheckOutRequest): Response<AttendanceRecordResponse> {
        return apiService.getCheckInCheckOutStatusAPI(request.toMultipartBody())
    }

    suspend fun insertLatLongAPI(request: DeviceTrackingRequest): Response<LocationTrackingResponse> {
        return apiService.insertLatLongAPI(request.toMultipartBody())
    }

    suspend fun getAttendanceInOutAPI(request: GetAttendanceInOutRequest): Response<LocationTrackingResponse> {
        return apiService.getAttendanceInOutAPI(request.toMultipartBody())
    }

    suspend fun getTextListAPI(request : TextListRequest): Response<Status> {
        return apiService.getTextListAPI(request.toMultipartBody())
    }

    suspend fun logOutWithFCMIdAPI(request : LogoutRequest): Response<LogoutResponse> {
        return apiService.logOutWithFCMIdAPI(request.toMultipartBody())
    }

}