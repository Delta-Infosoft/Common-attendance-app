package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetRatePerKMRequest(
    val travelingByForCarId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("TravelingbyforCarId", travelingByForCarId)
            .build()
    }
}