package com.i.common.attendance.network.request

import androidx.annotation.Keep
import okhttp3.MultipartBody
@Keep
data class GetUserValidRequest(
    val mobileNumber: String,
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNumber)
            .build()
    }
}