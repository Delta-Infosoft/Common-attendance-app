package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class CheckPJCEntryRequest(
    val mobileNo: String,
    val date: String,
    val fromDate: String?=null,
    val toDate: String?=null,
    val type: String
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("Date", date)
            .addFormDataPart("Type", type)
        fromDate?.let {
            builder.addFormDataPart("FromDate", it)
        }
        toDate?.let {
            builder.addFormDataPart("ToDate", it)
        }
        return builder.build()
    }
}