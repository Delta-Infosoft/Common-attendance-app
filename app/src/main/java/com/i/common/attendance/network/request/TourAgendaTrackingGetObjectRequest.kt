package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingGetObjectRequest(
    val businessCenterId: String,val empId : String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("BusiCntrId", businessCenterId)
            .addFormDataPart("EmpId", empId)
            .build()
    }
}