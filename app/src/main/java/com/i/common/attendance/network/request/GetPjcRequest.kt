package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetPjcRequest(val mobileNo: String,val monthYear: String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("MonthYear", monthYear)
            .build()
    }
}