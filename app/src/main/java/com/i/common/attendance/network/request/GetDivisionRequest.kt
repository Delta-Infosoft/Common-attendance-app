package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetDivisionRequest(
    val lgrId: String
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("LgrId", lgrId)

        return builder.build()
    }
}