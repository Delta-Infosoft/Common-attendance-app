package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class InsertCarAirApprovalRequest(
    val empId: String,
    val designation: String,
    val team: String,
    val deptId: String,
    val journeyFromDate: String,
    val journeyToDate: String,
    val fromPlace: String,
    val toPlace: String,
    val totalKMsActual: String,
    val totalChargedKMs: String,
    val travelingbyforCarId: String,
    val travellingBy: String,
    val perKMRate: String,
    val totalAmt: String,
    val tadaCityId: String,
    val cityTypeTextListId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("EmpId", empId)
            .addFormDataPart("Designation", designation)
            .addFormDataPart("Team", team)
            .addFormDataPart("DeptId", deptId)
            .addFormDataPart("JourneyFromDate", journeyFromDate)
            .addFormDataPart("JourneyToDate", journeyToDate)
            .addFormDataPart("FromPlace", fromPlace)
            .addFormDataPart("ToPlace", toPlace)
            .addFormDataPart("TotalKMsActual", totalKMsActual)
            .addFormDataPart("TotalChargedKMs", totalChargedKMs)
            .addFormDataPart("TravelingbyforCarId", travelingbyforCarId)
            .addFormDataPart("TravellingBy", travellingBy)
            .addFormDataPart("PerKMRate", perKMRate)
            .addFormDataPart("TotalAmt", totalAmt)
            .addFormDataPart("TADACityId", tadaCityId)
            .addFormDataPart("CityTypeTextListId", cityTypeTextListId)
            .build()
    }
}