package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.request.BusinessCenterNameRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.InsertJtdDetailsRequest
import com.i.common.attendance.network.request.InsertObjectiveRequest
import com.i.common.attendance.network.request.TourAgendaDealerNameRequest
import com.i.common.attendance.network.request.TourAgendaTrackingAddMeetingRequest
import com.i.common.attendance.network.request.TourAgendaTrackingDistrictRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetFactRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetObjectRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetRunningTaskDetailsRequest
import com.i.common.attendance.network.request.TourAgendaTrackingInOutDetailsRequest
import com.i.common.attendance.network.request.TourAgendaTrackingServiceCenterRequest
import com.i.common.attendance.network.request.TourAgendaTrackingStartEndMeetingRequest
import com.i.common.attendance.network.request.TourAgendaTrackingSubDealerNameRequest
import com.i.common.attendance.network.request.ValidateSundayRequest
import com.i.common.attendance.network.response.BusinessCenterNameResponse
import com.i.common.attendance.network.response.DistrictTourAgendaTrackingResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetInOutDetailsResponse
import com.i.common.attendance.network.response.GetStateResponse
import com.i.common.attendance.network.response.TourAgendaTrackingDealerNameResponse
import com.i.common.attendance.network.response.TourAgendaTrackingObjectiveResponse
import com.i.common.attendance.network.response.TourAgendaTrackingRunningTaskDetailsResponse
import com.i.common.attendance.network.response.TourAgendaTrackingServiceCenterResponse
import com.i.common.attendance.network.response.TourAgendaTrackingSubDealerNameResponse
import com.i.common.attendance.network.response.TourExpenseTrackingFacetsResponse
import com.i.common.attendance.network.response.ValidateSundayResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TourAgendaTrackingWithBaseViewUrlRepository @Inject constructor(@Named("DUKE") private val apiService: ApiService) {

    suspend fun getState(request: GetStateRequest): Response<GetStateResponse> {
        return apiService.getState(request.toMultipartBody())
    }
    suspend fun getTourAgendaTrackingDistrictAPI(request: TourAgendaTrackingDistrictRequest): Response<DistrictTourAgendaTrackingResponse> {
        return apiService.getTourAgendaTrackingDistrictAPI(request.toMultipartBody())
    }
    suspend fun getStation(request: BusinessCenterNameRequest): Response<BusinessCenterNameResponse> {
        return apiService.getStation(request.toMultipartBody())
    }

    suspend fun getDealerName(request: TourAgendaDealerNameRequest): Response<TourAgendaTrackingDealerNameResponse> {
        return apiService.getDealerName(request.toMultipartBody())
    }

    suspend fun getSubDealerName(request: TourAgendaTrackingSubDealerNameRequest): Response<TourAgendaTrackingSubDealerNameResponse> {
        return apiService.getSubDealer(request.toMultipartBody())
    }

    suspend fun getServiceCenters(request: TourAgendaTrackingServiceCenterRequest): Response<TourAgendaTrackingServiceCenterResponse> {
        return apiService.getServiceCenters(request.toMultipartBody())
    }

    suspend fun getRunningTaskDetails(request: TourAgendaTrackingGetRunningTaskDetailsRequest): Response<TourAgendaTrackingRunningTaskDetailsResponse> {
        return apiService.getRunningTaskDetails(request.toMultipartBody())
    }

    suspend fun getObjective(request: TourAgendaTrackingGetObjectRequest): Response<TourAgendaTrackingObjectiveResponse> {
        return apiService.getObjective(request.toMultipartBody())
    }

    suspend fun startEndMeeting(request: TourAgendaTrackingStartEndMeetingRequest): Response<FileUploadResponse> {
        return apiService.startEndMeeting(request.toMultipartBody())
    }

    suspend fun getInOutDetails(request: TourAgendaTrackingInOutDetailsRequest): Response<GetInOutDetailsResponse> {
        return apiService.getInOutDetails(request.toMultipartBody())
    }

    suspend fun insertObjective(request: InsertObjectiveRequest): Response<FileUploadResponse> {
        return apiService.insertObjective(request.toMultipartBody())
    }

    suspend fun insertJtdDetails(request: InsertJtdDetailsRequest): Response<FileUploadResponse> {
        return apiService.insertJtdDetails(request.toMultipartBody())
    }






}