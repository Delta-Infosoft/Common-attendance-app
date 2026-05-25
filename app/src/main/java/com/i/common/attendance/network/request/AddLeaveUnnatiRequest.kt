package com.i.common.attendance.network.request

import okhttp3.MultipartBody


data class AddLeaveUnnatiRequest(
    val frmDt: String,
    val toDt: String,
    val empId: String,
    val empName: String,
    val leaveReason: String,
    val leaveFor: String,
    val approvedDisapproved: String,
    val userId: String

) {

    fun toMultipartBody(): MultipartBody {

        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart("FrmDt", frmDt)
            .addFormDataPart("ToDt", toDt)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("EmpName", empName)
            .addFormDataPart("LeaveReson", leaveReason)
            .addFormDataPart("LeaveFor", leaveFor)
            .addFormDataPart("ApprovedDisapproved", approvedDisapproved)
            .addFormDataPart("UserId", userId)

            .build()
    }
}