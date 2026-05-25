package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetAttendanceInOutRequest(
    val mobileNo: String,
    val inTime: String = "",
    val outTime: String,
    val status: String = "",
    val remarks: String,
    val latitude: String,
    val longitude: String,
    val gpsStatus: String,
    val netStatus: String,
    val batteryStatus : String
) {

    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("InTime", inTime)
            .addFormDataPart("OutTime", outTime)
            .addFormDataPart("Status", status)
            .addFormDataPart("Remarks", remarks)
            .addFormDataPart("Lat", latitude)
            .addFormDataPart("Long", longitude)
            .addFormDataPart("GPSStatus", gpsStatus)
            .addFormDataPart("NetStatus", netStatus)
            .addFormDataPart("BattryStatus", batteryStatus)
            .build()
    }
}