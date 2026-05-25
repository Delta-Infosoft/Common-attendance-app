package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourListRequest(
    val mobileNo: String, val fromDate: String, val toDate: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("FromDt", fromDate)
            .addFormDataPart("ToDt", toDate)
            .build()
    }
}