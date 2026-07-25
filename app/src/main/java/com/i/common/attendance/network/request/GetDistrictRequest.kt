package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetDistrictRequest(
    val empId: String ?=null,
    val empName: String ?= null
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        empId?.let {
            builder.addFormDataPart("EmpId", it)
        }
        empName?.let {
            builder.addFormDataPart("EmpName", it)
        }

        return builder.build()
    }
}