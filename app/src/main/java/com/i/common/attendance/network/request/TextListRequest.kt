package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TextListRequest(
    val type: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Type", type)
            .build()
    }
}