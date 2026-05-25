package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourDistrictRequest(
    val stateId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("StateId", stateId)
            .build()
    }
}