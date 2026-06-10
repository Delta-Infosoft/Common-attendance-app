package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DealerDetailsAccountRequest(
    val userId: String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .build()
    }
}