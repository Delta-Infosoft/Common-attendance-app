package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ValidateSundayRequest(
    val empId: String,
    val requestDate: String? = null
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)

        requestDate?.let {
            builder.addFormDataPart("RequestDate", it)
        }

        return builder.build()
    }
}