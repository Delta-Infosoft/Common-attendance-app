package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourVoucherApprovalListUpdateStatusRequest(
    val empId: String, val remark: String, val approvalStatus: String, val expenseId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("Remarks", remark)
            .addFormDataPart("ApprovalStatus", approvalStatus)
            .addFormDataPart("ExpenseId", expenseId)
            .build()
    }
}