package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class SubmitSundayApprovalRequest(
    val empId: String,
    val approvedDisapproved: String, // "A" or "D"
    val approvedByUserId: String,
    val sundayRequestId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("ApprovedDisapproved", approvedDisapproved)
            .addFormDataPart("ApprovedDisapprovedbyUserId", approvedByUserId)
            .addFormDataPart("SundayRequestId", sundayRequestId)
            .build()
    }
}