package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ProductListRequest(
    val productGrpId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("ProductGrpId", productGrpId)
            .build()
    }
}