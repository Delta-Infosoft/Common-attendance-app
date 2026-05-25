package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import android.content.Context
import com.i.common.attendance.network.request.AddLeaveUnnatiRequest
import com.i.common.attendance.network.request.CheckDealerInOutStatusRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.network.request.GetTargetOutStandingRequest
import com.i.common.attendance.network.request.SaveDealerCheckInRequest
import com.i.common.attendance.network.request.SavePromotionalActivityRequest
import com.i.common.attendance.network.request.ViewLeaveApprovalUpdateUnnatiRequest
import com.i.common.attendance.network.request.ViewLeaveListUnnatiRequest
import com.i.common.attendance.network.response.CheckDealerInOutStatusResponse
import com.i.common.attendance.network.response.DailyTourDistrictResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetCustomerResponse
import com.i.common.attendance.network.response.GetLedgerPdfResponse
import com.i.common.attendance.network.response.LedgerPdfDataShowResponse
import com.i.common.attendance.network.response.TargetOutstandingResponse
import com.i.common.attendance.network.response.ViewLeaveUnnatiResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class PromotionalActivityRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun insertPromotionalActivity(request: SavePromotionalActivityRequest,context: Context): Response<FileUploadResponse> {
        return apiService.insertPromotionalActivity(request.toMultipartBody(context))
    }

    suspend fun getDistricts(): Response<DailyTourDistrictResponse> {
        return apiService.getDistricts()
    }

    suspend fun insertDealerCheckIn(request : SaveDealerCheckInRequest,context: Context): Response<FileUploadResponse> {
        return apiService.insertDealerCheckIn(request.toMultipartBody(context))
    }

    suspend fun checkDealerInOutStatus(request : CheckDealerInOutStatusRequest): Response<CheckDealerInOutStatusResponse> {
        return apiService.checkDealerInOutStatus(request.toMultipartBody())
    }

    suspend fun getTargetOutstanding(request : GetTargetOutStandingRequest): Response<TargetOutstandingResponse> {
        return apiService.getTargetOutstanding(request.toMultipartBody())
    }

    suspend fun insertLeaveRequestUnnati(request : AddLeaveUnnatiRequest): Response<FileUploadResponse> {
        return apiService.insertLeaveRequestUnnati(request.toMultipartBody())
    }

    suspend fun viewLeaveListUnnati(request : ViewLeaveListUnnatiRequest): Response<ViewLeaveUnnatiResponse> {
        return apiService.viewLeaveListUnnati(request.toMultipartBody())
    }

    suspend fun viewLeaveListApprovalUnnati(request : ViewLeaveListUnnatiRequest): Response<ViewLeaveUnnatiResponse> {
        return apiService.viewLeaveListApprovalUnnati(request.toMultipartBody())
    }

    suspend fun viewLeaveListApprovalUpdateUnnati(request : ViewLeaveApprovalUpdateUnnatiRequest): Response<FileUploadResponse> {
        return apiService.viewLeaveListApprovalUpdateUnnati(request.toMultipartBody())
    }



}