package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.request.GetPjcEventRequest
import com.i.common.attendance.network.response.PjcEventResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class WatermanPjcCalenderRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getPjcEvent(request: GetPjcEventRequest): Response<PjcEventResponse> {
        return apiService.getPjcEvent(request = request.toMultipartBody())
    }

    suspend fun getFollowUps(request: GetPjcEventRequest): Response<PjcEventResponse> {
        return apiService.getFollowUps(request = request.toMultipartBody())
    }

}