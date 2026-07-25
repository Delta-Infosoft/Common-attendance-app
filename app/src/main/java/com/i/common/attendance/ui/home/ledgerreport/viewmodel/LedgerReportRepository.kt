package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetDistrictRequest
import com.i.common.attendance.network.request.GetDivisionRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.network.response.GetCustomerResponse
import com.i.common.attendance.network.response.GetDistrictRespose
import com.i.common.attendance.network.response.GetDivisionRespose
import com.i.common.attendance.network.response.GetLedgerPdfResponse
import com.i.common.attendance.network.response.LedgerPdfDataShowResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class LedgerReportRepository @Inject constructor(@Named("FLAVOR_API") private val apiService: ApiService) {
    suspend fun getCustomer(request: GetCustomerRequest): Response<GetCustomerResponse> {
        return apiService.getCustomer(request.toMultipartBody())
    }

    suspend fun getLedgerReport(request: GetLedgerPdfRequest): Response<GetLedgerPdfResponse> {
        return apiService.getLedgerReport(request.toMultipartBody())
    }

    suspend fun ledgerReportShowPdf(request: GetLedgerPdfRequest): Response<LedgerPdfDataShowResponse> {
        return apiService.ledgerReportShowPdf(request.toMultipartBody())
    }

    suspend fun ledgerDistrictList(request: GetDistrictRequest): Response<GetDistrictRespose> {
        return apiService.getDistrictList(request.toMultipartBody())
    }

    suspend fun ledgerDivisionList(request: GetDivisionRequest): Response<GetDivisionRespose> {
        return apiService.getDivisionList(request.toMultipartBody())
    }

}