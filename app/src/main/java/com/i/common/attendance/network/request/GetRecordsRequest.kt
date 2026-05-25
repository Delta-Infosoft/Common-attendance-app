package com.i.common.attendance.network.request

import androidx.annotation.Keep
import okhttp3.MultipartBody
@Keep
data class GetRecordsRequest(
    val mobileNumber: String,
    val month: String,
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNumber)
            //.addFormDataPart("Month", month)
            .build()
    }
}