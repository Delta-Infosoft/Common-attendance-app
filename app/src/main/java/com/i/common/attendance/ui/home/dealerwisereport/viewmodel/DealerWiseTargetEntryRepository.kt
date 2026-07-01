package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import com.i.common.attendance.network.request.DealerDetailsAccountRequest
import com.i.common.attendance.network.request.GetMonthForTargetRequest
import com.i.common.attendance.network.request.SubmitDealerWiseTargetRequest
import com.i.common.attendance.network.response.DealerDetailsAccountResponse
import com.i.common.attendance.network.response.DealerWiseTargetResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.MonthListResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class DealerWiseTargetEntryRepository @Inject constructor(@Named("UNNATI_VIEWER") private val unnatiViewerApi: ApiService) {

    suspend fun getDealerDetailsAccount(request: DealerDetailsAccountRequest): Response<DealerDetailsAccountResponse> {
        return unnatiViewerApi.dealerDetailsAccount(request.toMultipartBody())
    }

    suspend fun getMonthAPI(request :GetMonthForTargetRequest): Response<MonthListResponse> {
        return unnatiViewerApi.getMonthForTarget(request.toMultipartBody())
    }

    suspend fun dealerWiseTargetType(): Response<DealerWiseTargetResponse> {
        return unnatiViewerApi.dealerWiseTargetType()
    }

    suspend fun submitDealerWiseTarget(request: SubmitDealerWiseTargetRequest): Response<FileUploadResponse> {
        return unnatiViewerApi.submitDealerWiseTarget(request.toMultipartBody())
    }

}