package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetTargetOutStandingRequest(
    val empId: String,
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .build()
    }
}