package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetTravelDukeRequest(
    val isCarYes: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("IsCarYes", isCarYes)
            .build()
    }
}