package com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel

import com.i.common.attendance.network.request.TourVoucherApprovalListUpdateStatusRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.TourVoucherResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TourVoucherApprovalRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getTourVoucherApprovalListAPI(request: TourVoucherRequest): Response<TourVoucherResponse> {
        return apiService.getTourVoucherApprovalListAPI(request.toMultipartBody())
    }
        suspend fun updateExpenseStatus(request: TourVoucherApprovalListUpdateStatusRequest): Response<FileUploadResponse> {
            return apiService.updateExpenseStatus(request.toMultipartBody())
        }

}