package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class BusinessCenterNameRequest(
    val empId: String, val stateId : String?=null,val districtName : String?=null
) {
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)

        stateId?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("StateTextListId", it)
        }

        districtName?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("District", it)
        }

        return builder.build()
    }
}