package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ForgotPasswordRequest(
    val userName: String,
    val imei: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserName", userName)
            .addFormDataPart("IMEI", imei)
            .build()
    }
}