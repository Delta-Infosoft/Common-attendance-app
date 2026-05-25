package com.i.common.attendance.ui.home.myportfolio.viewmodel

import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.request.ViewPortFolioRequest
import com.i.common.attendance.network.response.EmployeeResponse
import com.i.common.attendance.network.response.ViewPortFolioResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class MyPortfolioRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getEmployeeParam(request: EmployeeRequest): Response<EmployeeResponse> {
        return apiService.getEmployeeParam(request.toMultipartBody())
    }

    suspend fun getViewPortFolio(request: ViewPortFolioRequest): Response<ViewPortFolioResponse> {
        return apiService.getViewPortFolio(request.toMultipartBody())
    }


}