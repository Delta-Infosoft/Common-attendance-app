package com.i.common.attendance.ui.home.dailytour.viewmodel

import android.content.Context
import com.i.common.attendance.network.request.DailyTourAddDetailsDukeRequest
import com.i.common.attendance.network.request.DailyTourAddDetailsRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.DailyTourFlotechRequest
import com.i.common.attendance.network.request.DailyTourListRequest
import com.i.common.attendance.network.response.DailTourListResponse
import com.i.common.attendance.network.response.DailyTourDealerCategoryResponse
import com.i.common.attendance.network.response.DailyTourDealerNameResponse
import com.i.common.attendance.network.response.DailyTourDistrictResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class DailyTourRepository@Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getDailyDetailsListAPI(request: DailyTourListRequest): Response<DailTourListResponse> {
        return apiService.getDailyDetailsListAPI(request.toMultipartBody())
    }
    suspend fun getDealerCategoryAPI(request: DailyTourDealerCategoryRequest): Response<DailyTourDealerCategoryResponse> {
        return apiService.getDealerCategoryAPI(request.toMultipartBody())
    }
    suspend fun getDealerNameAPI(request: DailyTourDealerNameRequest): Response<DailyTourDealerNameResponse> {
        return apiService.getDealerNameAPI(request.toMultipartBody())
    }
    suspend fun getDistrictAPI(request: DailyTourDistrictRequest): Response<DailyTourDistrictResponse> {
        return apiService.getDistrictAPI(request.toMultipartBody())
    }
    suspend fun insertDailyDetailsAPI(request: DailyTourAddDetailsRequest,context: Context): Response<FileUploadResponse> {
        return apiService.insertDailyDetailsAPI(request.toMultipartBody(context))
    }
    suspend fun insertDailyDetailsDukeAPI(request: DailyTourAddDetailsDukeRequest): Response<FileUploadResponse> {
        return apiService.insertDailyDetailsAPI(request.toMultipartBody())
    }

    /*================================== Flotech ==========================================*/
    /*============================= Daily Tour Details ======================================*/

    suspend fun insertDailyTourDetailsFlotech(request: DailyTourFlotechRequest): Response<FileUploadResponse> {
        return apiService.insertDailyTourDetailsFlotech(request.toMultipartBody())
    }



}