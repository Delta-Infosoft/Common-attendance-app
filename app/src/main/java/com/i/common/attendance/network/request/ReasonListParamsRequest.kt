package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class ReasonListParamsRequest(val reasonWiseDetailId: String,val planForTextListId: String, val transactionType: String,val pJCEntryId : String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("ReasonWiseDetailId", reasonWiseDetailId)
            .addFormDataPart("Planfortextlistid", planForTextListId)
            .addFormDataPart("TransactionType", transactionType)
            .addFormDataPart("PJCEntryId", pJCEntryId)
            .build()
    }
}