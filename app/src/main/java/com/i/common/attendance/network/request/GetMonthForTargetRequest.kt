package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetMonthForTargetRequest(
    val userId: String,
    val dealerId : String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .addFormDataPart("DealerId", dealerId)
            .build()
    }
}