package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DeviceTrackingRequest(
    val mobileNo: String,
    val latitude: Double,
    val longitude: Double,
    val batteryStatus: String,
    val gpsStatus: String,
    val netStatus: String,
    val appVersion: String,
    val insertedOn: String,
    val modelName: String,
    val androidVersion: String
){
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("Lat", latitude.toString())
            .addFormDataPart("Long", longitude.toString())
            .addFormDataPart("BattryStatus", batteryStatus)
            .addFormDataPart("GPSStatus", gpsStatus)
            .addFormDataPart("NetStatus", netStatus)
            .addFormDataPart("AppVersion", appVersion)
            .addFormDataPart("InsertedOn", insertedOn)
            .addFormDataPart("ModelName", modelName)
            .addFormDataPart("Androidversion", androidVersion)
            .build()
    }
}