package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import com.i.common.attendance.network.request.FacetRequest
import com.i.common.attendance.network.response.FacetsResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class FactRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getFacetReport(request: FacetRequest): Response<FacetsResponse> {
        return apiService.getFacet(request.toMultipartBody())
    }
}