package com.i.common.attendance.network.request

import okhttp3.MultipartBody


data class ViewLeaveListUnnatiRequest(
    val frmDt: String,
    val toDt: String,
    val empId: String,
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart("FrmDt", frmDt)
            .addFormDataPart("ToDt", toDt)
            .addFormDataPart("EmpId", empId)


            .build()
    }
}