package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.SubmitSundayApprovalRequest
import com.i.common.attendance.network.request.TourAgendaTrackingAddMeetingRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetFactRequest
import com.i.common.attendance.network.request.ValidateSundayRequest
import com.i.common.attendance.network.request.WeekOffRequest
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetUserRightsResponse
import com.i.common.attendance.network.response.SundayRequestListResponse
import com.i.common.attendance.network.response.TourExpenseTrackingFacetsResponse
import com.i.common.attendance.network.response.ValidateSundayResponse
import com.i.common.attendance.network.response.WeekOffListResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TourAgendaTrackingWithDefaultBaseUrlRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getFacets(request: TourAgendaTrackingGetFactRequest): Response<TourExpenseTrackingFacetsResponse> {
        return apiService.getFacets(request.toMultipartBody())
    }

    suspend fun insertTourTrackingDetails(request: TourAgendaTrackingAddMeetingRequest): Response<FileUploadResponse> {
        return apiService.insertTourTrackingDetails(request.toMultipartBody())
    }

    suspend fun getUserRights(request: GetStateRequest): Response<GetUserRightsResponse> {
        return apiService.getUserRights(request.toMultipartBody())
    }

    /*================  WeekOff  =================*/
    suspend fun validateSunday(request: ValidateSundayRequest): Response<ValidateSundayResponse> {
        return apiService.validateSunday(request.toMultipartBody())
    }

    suspend fun submitWeekOff(request: WeekOffRequest): Response<FileUploadResponse> {
        return apiService.submitWeekOff(request.toMultipartBody())
    }

    suspend fun getSundayRequestList(request: ValidateSundayRequest): Response<WeekOffListResponse> {
        return apiService.getSundayRequestList(request.toMultipartBody())
    }

    suspend fun getApprovalListSundayRequest(): Response<SundayRequestListResponse> {
        return apiService.getApprovalListSundayRequest()
    }

    suspend fun submitSundayApproval(request: SubmitSundayApprovalRequest): Response<FileUploadResponse> {
        return apiService.submitSundayApproval(request.toMultipartBody())
    }



}