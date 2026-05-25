package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class CheckInCheckOutRequest(val userName: String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserName", userName)
            .build()
    }
}