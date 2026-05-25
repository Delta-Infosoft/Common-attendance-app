package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class GetCarAirApprovalListResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result")  val result:  Any?    = null
)

data class CarAirApprovalItem(
    @SerializedName("CarAirApprovalId") val carAirApprovalId: String?,
    @SerializedName("Name") val name: String?,
    @SerializedName("Designation") val designation: String?,
    @SerializedName("TotalKMsActual") val totalKMsActual: String?,
    @SerializedName("PerKMRate") val perKMRate: String?,
    @SerializedName("TotalAmt") val totalAmt: String?,
    @SerializedName("JourneyFromDate") val journeyFromDate: String?,
    @SerializedName("JourneyToDate") val journeyToDate: String?,
    @SerializedName("FromPlace") val fromPlace: String?,
    @SerializedName("ToPlace") val toPlace: String?,
    @SerializedName("Date") val date: String?,
    @SerializedName("No") val no: String?,
    @SerializedName("Team") val team: String?,
    @SerializedName("ApprovedDisapproved") val approvedDisapproved: String?,
    @SerializedName("Department") val department: String?,
    @SerializedName("CarAirApprovalcount") val carAirApprovalcount: String?
)