package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaDealerNameRequest(
    val empId: String,
    val busignessCenterId: String,
    val dealerType : String,
    val dealerName : String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("BusiCntrId", busignessCenterId)
            .addFormDataPart("DealerType", dealerType)
            .addFormDataPart("DealerName", dealerName)
            .build()
    }
}