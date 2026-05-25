package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class InsertOrderRequest(
    val empId: String,
    val itemArray: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("ItemArray", itemArray)
            .build()
    }
}