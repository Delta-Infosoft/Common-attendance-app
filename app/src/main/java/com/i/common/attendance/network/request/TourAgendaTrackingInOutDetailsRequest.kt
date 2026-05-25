package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingInOutDetailsRequest(
    val dealerId: String, val empId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("DealerId", dealerId)
            .addFormDataPart("EmpId", empId)
            .build()
    }
}