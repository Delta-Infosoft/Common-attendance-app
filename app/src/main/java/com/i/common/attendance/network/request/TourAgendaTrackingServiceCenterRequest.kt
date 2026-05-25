package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingServiceCenterRequest(
    val stateName: String? = null
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        stateName?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("State", it)
        }

        return builder.build()
    }
}