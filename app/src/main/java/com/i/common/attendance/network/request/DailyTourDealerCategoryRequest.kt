package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourDealerCategoryRequest(
    val type: String, val deptId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Type", type)
            .addFormDataPart("DeptId", deptId)
            .build()
    }
}