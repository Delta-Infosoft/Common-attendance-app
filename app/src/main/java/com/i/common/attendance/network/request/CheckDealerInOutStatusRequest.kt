package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class CheckDealerInOutStatusRequest(
    val userName: String,
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Username", userName)
            .build()
    }
}