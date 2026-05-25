package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetDistrictPjcRequest(val userId: String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .build()
    }
}