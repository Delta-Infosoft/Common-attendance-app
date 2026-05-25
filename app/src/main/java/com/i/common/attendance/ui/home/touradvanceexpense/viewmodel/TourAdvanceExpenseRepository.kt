package com.i.common.attendance.ui.home.touradvanceexpense.viewmodel

import com.i.common.attendance.network.request.AddTourAdvanceExpenseRequest
import com.i.common.attendance.network.request.TourAdvanceExpenseListRequest
import com.i.common.attendance.network.response.AddTourAdvanceExpenseResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.TourAdvanceExpenseResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TourAdvanceExpenseRepository@Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    /*================================== Flotech ==========================================*/
    /*============================= Daily Tour Details ======================================*/

    suspend fun insertTourAdvanceExpense(request: AddTourAdvanceExpenseRequest): Response<AddTourAdvanceExpenseResponse> {
        return apiService.insertTourAdvanceExpense(request.toMultipartBody())
    }

    suspend fun updateTourAdvanceExpense(request: AddTourAdvanceExpenseRequest): Response<AddTourAdvanceExpenseResponse> {
        return apiService.updateTourAdvanceExpense(request.toMultipartBody())
    }

    suspend fun tourAdvanceList(request: TourAdvanceExpenseListRequest): Response<TourAdvanceExpenseResponse> {
        return apiService.getTourAdvanceExpenseList(request.toMultipartBody())
    }


}