package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.request.GetCityTypeListDukeRequest
import com.i.common.attendance.network.request.GetRatePerKMRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.GetTravelDukeRequest
import com.i.common.attendance.network.request.GetVoucherNoDukeRequest
import com.i.common.attendance.network.request.InsertCarAirApprovalRequest
import com.i.common.attendance.network.request.UpdateCarAirApprovalStatusRequest
import com.i.common.attendance.network.response.EmployeeDukeResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetCarAirApprovalListResponse
import com.i.common.attendance.network.response.GetCitiesResponse
import com.i.common.attendance.network.response.GetRatePerKMApiResponse
import com.i.common.attendance.network.response.TravelResponse
import com.i.common.attendance.network.response.VoucherNoResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class CarAirApprovalRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getEmpData(request: GetStateRequest): Response<EmployeeDukeResponse> {
        return apiService.getEmpData(request.toMultipartBody())
    }

    suspend fun getNewVoucherNo(request: GetVoucherNoDukeRequest): Response<VoucherNoResponse> {
        return apiService.getNewVoucherNo(request.toMultipartBody())
    }

    suspend fun getTravellingByCar(request: GetTravelDukeRequest): Response<TravelResponse> {
        return apiService.getTravellingByCar(request.toMultipartBody())
    }

    suspend fun getRateForPerKmCarAirApproval(request: GetRatePerKMRequest): Response<GetRatePerKMApiResponse> {
        return apiService.getRateForPerKmCarAirApproval(request.toMultipartBody())
    }

    suspend fun getCityType(request: GetCityTypeListDukeRequest): Response<TravelResponse> {
        return apiService.getCityType(request.toMultipartBody())
    }

    suspend fun getCity(): Response<GetCitiesResponse> {
        return apiService.getCity()
    }

    suspend fun insertCarApproval(request: InsertCarAirApprovalRequest): Response<FileUploadResponse> {
        return apiService.insertCarApproval(request.toMultipartBody())
    }

    suspend fun getCarAirApprovalList(): Response<GetCarAirApprovalListResponse> {
        return apiService.getCarAirApprovalList()
    }

    suspend fun updateCarAirApprovalStatus(request : UpdateCarAirApprovalStatusRequest): Response<FileUploadResponse> {
        return apiService.updateCarAirApprovalStatus(request.toMultipartBody())
    }
}