package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DropdownRequest(
    val Group: String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Group", Group)
            .build()
    }
}