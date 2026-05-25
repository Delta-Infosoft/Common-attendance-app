package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingGetRunningTaskDetailsRequest(
    val empId: String,
    val dealerId: String,
    val businessCenterId: String,
    val stateId: String,
    val districtId: String,
    val dealerCategoryId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("DealerId", dealerId)
            .addFormDataPart("BusinessCenterId", businessCenterId)
            .addFormDataPart("StateId", stateId)
            .addFormDataPart("DistrictId", districtId)
            .addFormDataPart("DealerCategoryId", dealerCategoryId)
            .build()
    }
}