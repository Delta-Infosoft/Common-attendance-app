package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class RateRequest(
    val lgrId: String,
    val productId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("LgrId", lgrId)
            .addFormDataPart("ProductId", productId)
            .build()
    }
}