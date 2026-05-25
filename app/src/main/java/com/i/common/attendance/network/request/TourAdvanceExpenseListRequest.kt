package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAdvanceExpenseListRequest(
    val fromDt: String,
    val toDt: String
) {

    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .apply {
                addFormDataPart("FromDt", fromDt)
                addFormDataPart("ToDt", toDt)
            }
            .build()
    }
}
