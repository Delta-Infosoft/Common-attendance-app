package com.i.common.attendance.ui.home.orderbook.viewmodel

import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.InsertOrderRequest
import com.i.common.attendance.network.request.ProductListRequest
import com.i.common.attendance.network.request.RateRequest
import com.i.common.attendance.network.response.CustomerResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.OrderListResponse
import com.i.common.attendance.network.response.ProductResponse
import com.i.common.attendance.network.response.RateResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class OrderBookRepository  @Inject constructor(@Named("FLOTECH") private val apiService: ApiService) {

    suspend fun getOrderList(request: GetStateRequest): Response<OrderListResponse> {
        return apiService.getOrderList(request.toMultipartBody())
    }

    suspend fun getCustomerViewerParam(request: GetStateRequest): Response<CustomerResponse> {
        return apiService.getCustomerViewerParam(request.toMultipartBody())
    }

    suspend fun getProductParam(request: ProductListRequest): Response<ProductResponse> {
        return apiService.getProductParam(request.toMultipartBody())
    }

    suspend fun getRate(request: RateRequest): Response<RateResponse> {
        return apiService.getRate(request.toMultipartBody())
    }

    suspend fun insertOrderEntry(request: InsertOrderRequest): Response<FileUploadResponse> {
        return apiService.insertOrderEntry(request.toMultipartBody())
    }


}