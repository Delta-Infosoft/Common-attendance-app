package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingGetFactRequest(
    val parameter: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Parameter", parameter)
            .build()
    }
}