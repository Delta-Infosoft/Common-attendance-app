package com.i.common.attendance.ui.home.newcustomerdealer.viewmodel

import android.content.Context
import com.i.common.attendance.network.request.InsertVisitRequest
import com.i.common.attendance.network.request.SelectPortfolioRequest
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.SelectPortfolioResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class PortfolioRepository @Inject constructor(@Named("DEFAULT")
                                              private val apiService: ApiService
) {

    suspend fun getSelectPortFolio(request: SelectPortfolioRequest): Response<SelectPortfolioResponse> {
        return apiService.getSelectPortFolio(request.toMultipartBody())
    }

    suspend fun insertVisit(request: InsertVisitRequest, context: Context): Response<FileUploadResponse> {
        return apiService.insertVisit(request.toMultipartBody(context = context))
    }

    suspend fun updatePortFolioAPI(request: InsertVisitRequest, context: Context): Response<FileUploadResponse> {
        return apiService.updatePortFolioAPI(request.toMultipartBody(context = context))
    }
}