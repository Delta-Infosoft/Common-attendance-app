package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourDealerNameRequest(
    val empId: String, val dealerType : String,val busignessCenterId: String?=null
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("DealerType", dealerType)

        busignessCenterId?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("BusinessCenterId", it)
        }

        return builder.build()
    }
}