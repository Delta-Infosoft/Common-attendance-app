package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetStateRequest(
    val empId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .build()
    }
}