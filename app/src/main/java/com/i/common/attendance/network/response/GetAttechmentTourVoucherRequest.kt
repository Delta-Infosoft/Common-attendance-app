package com.i.common.attendance.network.response

import okhttp3.MultipartBody

data class GetAttechmentTourVoucherRequest(
    val attachmentType: String,val recordId : String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("AttachmentType", attachmentType)
            .addFormDataPart("RecordId", recordId)
            .build()
    }
}