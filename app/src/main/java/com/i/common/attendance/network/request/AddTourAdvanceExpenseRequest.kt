package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class AddTourAdvanceExpenseRequest(
    val empId: String,
    val requestDt: String,
    val advanceAmount: String,
    val remarks: String,
    val advanceExpenseId: String? = null
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("RequestDt", requestDt)
            .addFormDataPart("AdvanceAmount", advanceAmount)
            .addFormDataPart("Remarks", remarks)

        advanceExpenseId?.let {
            builder.addFormDataPart("AdvanceExpenseId", it)
        }

        return builder.build()
    }
}