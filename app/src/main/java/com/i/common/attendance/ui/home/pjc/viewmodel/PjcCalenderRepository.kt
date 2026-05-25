package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.request.GetDistrictPjcRequest
import com.i.common.attendance.network.request.GetPjcEventRequest
import com.i.common.attendance.network.request.GetPjcRequest
import com.i.common.attendance.network.request.GetSqlQueryForDropdownParamRequest
import com.i.common.attendance.network.request.InsertPjcEventRequest
import com.i.common.attendance.network.response.GetDistrictPjcResponse
import com.i.common.attendance.network.response.HolidayWeekOffResponse
import com.i.common.attendance.network.response.InsertPjcEventResponse
import com.i.common.attendance.network.response.LoadDropDownListResponse
import com.i.common.attendance.network.response.PjcEventResponse
import com.i.common.attendance.network.response.PjcResponse
import com.i.common.attendance.network.response.PlanForListResponse
import com.i.common.attendance.network.response.ReasonListParamsResponse
import com.i.common.attendance.network.response.ReasonResponse
import com.i.common.attendance.network.service.ApiService
import com.i.common.attendance.network.request.ReasonListParamsRequest
import com.i.common.attendance.network.request.ReasonRequest
import com.i.common.attendance.network.response.ServerTimeResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class PjcCalenderRepository @Inject constructor(@Named("DEFAULT")private val apiService: ApiService) {

    suspend fun getPjc(request: GetPjcRequest): Response<PjcResponse> {
        return apiService.getPjcApi(request.toMultipartBody())
    }

    suspend fun getHolidayWeekOffParam(request: GetPjcRequest): Response<HolidayWeekOffResponse> {
        return apiService.getHolidayWeekOffParam(request.toMultipartBody())
    }

    suspend fun getPjcEvent(request: GetPjcEventRequest): Response<PjcEventResponse> {
        return apiService.getPjcEvent(request = request.toMultipartBody())
    }

    suspend fun getPlanForList(): Response<PlanForListResponse> {
        return apiService.getApiPlanForList()
    }

    suspend fun getReasonApi(request : ReasonRequest): Response<ReasonResponse> {
        return apiService.getReasonApi(request = request.toMultipartBody())
    }

    suspend fun getDistrictPjcApi(request : GetDistrictPjcRequest): Response<GetDistrictPjcResponse> {
        return apiService.getDistrictPjcApi(request = request.toMultipartBody())
    }

    suspend fun getReasonListParam(request : ReasonListParamsRequest): Response<ReasonListParamsResponse> {
        return apiService.getReasonListParam(request = request.toMultipartBody())
    }

    suspend fun getSqlQueryForDropdownParam(request : GetSqlQueryForDropdownParamRequest): Response<LoadDropDownListResponse> {
        return apiService.getSqlQueryForDropdownParam(request = request.toMultipartBody())
    }

    suspend fun insertPJCEntry(request : InsertPjcEventRequest): Response<InsertPjcEventResponse> {
        return apiService.insertPJCEntry(request = request.toMultipartBody())
    }

    suspend fun getServerTime(): Response<ServerTimeResponse> {
        return apiService.getServerTime()
    }





}