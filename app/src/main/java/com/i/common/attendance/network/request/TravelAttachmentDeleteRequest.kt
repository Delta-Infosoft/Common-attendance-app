package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TravelAttachmentDeleteRequest(
    val recordId: String,
    val fuId: String,
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("RecordId", recordId)
            .addFormDataPart("FUId", fuId)
            .build()
    }
}