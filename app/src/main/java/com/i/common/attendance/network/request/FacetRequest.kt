package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class FacetRequest(
    val type: String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Type", type)
            .build()
    }
}