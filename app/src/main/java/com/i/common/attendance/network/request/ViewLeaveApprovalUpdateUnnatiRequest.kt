package com.i.common.attendance.network.request

import okhttp3.MultipartBody


data class ViewLeaveApprovalUpdateUnnatiRequest(
    val leaveRequestId: String,
    val userId: String,
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("LeaveRequestId", leaveRequestId)
            .addFormDataPart("UserId", userId)
            .build()
    }
}