package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetMonthForTargetRequest(
    val userId: String,
    val dealerId : String?=null
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .apply {
                dealerId?.let {
                    addFormDataPart("DealerId", it)
                }
            }
            .build()
    }

}