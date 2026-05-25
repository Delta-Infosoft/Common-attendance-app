package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import android.content.Context
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.DropdownRequest
import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.request.FieldVisitRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.network.request.SaveTourVoucherRequest
import com.i.common.attendance.network.request.TourVoucherEditDataRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.network.request.TravelAttachmentDeleteRequest
import com.i.common.attendance.network.request.TravelingByRequest
import com.i.common.attendance.network.response.ApiResponse
import com.i.common.attendance.network.response.CheckPJCEntryResponse
import com.i.common.attendance.network.response.EmployeeResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetAttechmentTourVoucherRequest
import com.i.common.attendance.network.response.NameDropdownResponse
import com.i.common.attendance.network.response.PjcDateResponse
import com.i.common.attendance.network.response.PjcPermissionResponse
import com.i.common.attendance.network.response.TourVoucherResponse
import com.i.common.attendance.network.response.TravelingByResponse
import com.i.common.attendance.network.response.UnPlanApiResponse
import com.i.common.attendance.network.response.UploadAttachmentResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TourVoucherRepository @Inject constructor(@Named("DEFAULT") private val apiService: ApiService) {

    suspend fun getEmployeeParam(request: EmployeeRequest): Response<EmployeeResponse> {
        return apiService.getEmployeeParam(request.toMultipartBody())
    }

    suspend fun getTravelingByParam(request: TravelingByRequest): Response<TravelingByResponse> {
        return apiService.getTravelingByParam(request.toMultipartBody())
    }

    suspend fun getTourVoucherList(request: TourVoucherRequest): Response<TourVoucherResponse> {
        return apiService.tourVoucherList(request.toMultipartBody())
    }

    suspend fun insertTourVoucher(request: SaveTourVoucherRequest, context: Context): Response<UnPlanApiResponse> {
        return apiService.insertTourVoucher(request.toMultipartBody(context))
    }

    suspend fun insertTourEditVoucher(request: SaveTourVoucherRequest,context: Context): Response<UnPlanApiResponse> {
        return apiService.insertTourEditVoucher(request.toMultipartBody(context))
    }

    suspend fun checkPJCEntry(request: CheckPJCEntryRequest): Response<CheckPJCEntryResponse> {
        return apiService.checkPJCAndDTD(request.toMultipartBody())
    }

    suspend fun getCommonDropDownData(request: DropdownRequest): Response<ApiResponse> {
        return apiService.getCommonDropDownData(request.toMultipartBody())
    }

    suspend fun getCommonDropDownDistrict(): Response<NameDropdownResponse> {
        return apiService.getCommonDropDownDistrict()
    }

    suspend fun getCommonDropDownCity(): Response<NameDropdownResponse> {
        return apiService.getCommonDropDownCity()
    }

    suspend fun apiGetEmployeeData(): Response<EmployeeResponse> {
        return apiService.apiGetEmployeeData()
    }

    suspend fun insertFieldVisitData(request : FieldVisitRequest): Response<UnPlanApiResponse> {
        return apiService.insertFieldVisitData(request.toMultipartBody())
    }

    suspend fun getBackDatedRightAPI(request : PjcDateRequest): Response<PjcDateResponse> {
        return apiService.getBackDatedRightAPI(request.toMultipartBody())
    }

    suspend fun getWithoutPJCTourRightsAPI(request : PjcDateRequest): Response<PjcPermissionResponse> {
        return apiService.getWithoutPJCTourRightsAPI(request.toMultipartBody())
    }

    suspend fun getTourDetailsAPI(request : TourVoucherEditDataRequest): Response<TourVoucherResponse> {
        return apiService.getTourDetailsAPI(request.toMultipartBody())
    }

    suspend fun getAttachmentFileParam(request : GetAttechmentTourVoucherRequest): Response<UploadAttachmentResponse> {
        return apiService.getAttachmentFileParam(request.toMultipartBody())
    }

    suspend fun deleteTravelAttachment(request: TravelAttachmentDeleteRequest): Response<FileUploadResponse> {
        return apiService.deleteTravelAttachment(request.toMultipartBody())
    }


}