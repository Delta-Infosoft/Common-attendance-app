package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourVoucherRequest(
    val MobileNo: String,
    val FromDt: String,
    val ToDt: String,
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", MobileNo)
            .addFormDataPart("FromDt", FromDt)
            .addFormDataPart("ToDt", ToDt)
            .build()
    }
}