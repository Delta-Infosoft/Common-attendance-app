package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingDistrictRequest(
    val empId: String, val stateName: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("State", stateName)
            .build()
    }
}