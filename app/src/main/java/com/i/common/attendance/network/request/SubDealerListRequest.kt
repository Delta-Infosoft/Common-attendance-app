package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class SubDealerListRequest(
    val empId: String,val busCenterId : String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("BusiCntrId", busCenterId)
            .build()
    }
}