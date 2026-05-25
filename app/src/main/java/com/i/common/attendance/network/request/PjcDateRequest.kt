package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class PjcDateRequest(
    val empId: String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpID", empId)
            .build()
    }
}