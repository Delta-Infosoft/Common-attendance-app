package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class SelectPortfolioRequest(
    val id: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Id", id)
            .build()
    }
}