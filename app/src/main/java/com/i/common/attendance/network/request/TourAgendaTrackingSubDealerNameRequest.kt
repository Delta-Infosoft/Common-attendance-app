package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingSubDealerNameRequest(
    val empId: String, val busignessCenterId: String, val dealerType:String?=null
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("BusiCntrId", busignessCenterId)

        dealerType?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("DealerType", it)
        }

        return builder.build()
    }
}