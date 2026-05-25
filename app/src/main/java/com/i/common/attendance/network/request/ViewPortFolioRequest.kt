package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ViewPortFolioRequest(
    val mobileNo: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .build()
    }
}