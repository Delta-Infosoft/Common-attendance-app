package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.network.response.GetCustomerResponse
import com.i.common.attendance.network.response.GetLedgerPdfResponse
import com.i.common.attendance.network.response.LedgerPdfDataShowResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class LedgerReportRepository @Inject constructor(@Named("MASCOT") private val apiService: ApiService) {
    suspend fun getCustomer(request: GetCustomerRequest): Response<GetCustomerResponse> {
        return apiService.getCustomer(request.toMultipartBody())
    }

    suspend fun getLedgerReport(request: GetLedgerPdfRequest): Response<GetLedgerPdfResponse> {
        return apiService.getLedgerReport(request.toMultipartBody())
    }

    suspend fun ledgerReportShowPdf(request: GetLedgerPdfRequest): Response<LedgerPdfDataShowResponse> {
        return apiService.ledgerReportShowPdf(request.toMultipartBody())
    }
}