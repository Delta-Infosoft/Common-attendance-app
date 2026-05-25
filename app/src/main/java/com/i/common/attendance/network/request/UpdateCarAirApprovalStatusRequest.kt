package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class UpdateCarAirApprovalStatusRequest(
    val userId: String,
    val approvedDisapproved: String, // "A" or "D"
    val carAirApprovalId: String,
    val suspenseApproved: String,    // "true" or "false"
    val remarks: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .addFormDataPart("approvedDisapproved", approvedDisapproved)
            .addFormDataPart("CarAirApprovalId", carAirApprovalId)
            .addFormDataPart("SuspenseApproved", suspenseApproved)
            .addFormDataPart("Remarks", remarks)
            .build()
    }
}