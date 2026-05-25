package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class WeekOffRequest(
    val empId: String,
    val date: String,
    val requestDate: String,
    val empName: String,
    val mobileNo: String,
    val reason: String,
    val insertedByUserId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("Date", date)
            .addFormDataPart("RequestDate", requestDate)
            .addFormDataPart("EmpName", empName)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("Reason", reason)
            .addFormDataPart("InsertedByUserId", insertedByUserId)
            .build()
    }
}