package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ReasonRequest(val planForTextListId: String,val unplanRequired: String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("PlanForTextListId", planForTextListId)
            .addFormDataPart("UnplanRequired", unplanRequired)
            .build()
    }
}