package com.i.common.attendance.network.request

import androidx.annotation.Keep
import okhttp3.MultipartBody

@Keep
data class LoginRequest(
    val userName: String,
    val imei: String,
    val fcmId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserName", userName)
            .addFormDataPart("IMEI", imei)
            .addFormDataPart("FCMId", fcmId)
            .build()
    }
}
