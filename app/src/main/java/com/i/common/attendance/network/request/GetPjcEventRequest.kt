package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetPjcEventRequest(
    val date: String?=null,
    val mobileNo: String) {
    fun toMultipartBody(): MultipartBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        date?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("Date", it)
        }
        builder.addFormDataPart("MobileNo", mobileNo)

        return builder.build()
    }
}