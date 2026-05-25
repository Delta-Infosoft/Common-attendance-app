package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class InsertPjcEventRequest(
    val date: String,
    val place: String,
    val notes: String,
    val mobileNo: String,
    val monthYear: String,
    val nightHault: String,
    val type: String? = null
){
    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Date", date)
            .addFormDataPart("Place", place)
            .addFormDataPart("Notes", notes)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("MonthYear", monthYear)
            .addFormDataPart("NightHault", nightHault)

        if (!type.isNullOrEmpty()) {
            builder.addFormDataPart("Type", type)
        }

        return builder.build()
    }
}