package com.i.common.attendance.ui.home.attendancereport.viewmodel

import com.i.common.attendance.network.response.MonthListResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class AttendanceReportRepository@Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getMonthAPI(): Response<MonthListResponse> {
        return apiService.getMonthAPI()
    }
}