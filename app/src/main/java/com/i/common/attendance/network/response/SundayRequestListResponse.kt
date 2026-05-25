package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class SundayRequestListResponse(
    @SerializedName("status")  val status:  String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null
)

data class SundayRequestItem(
    @SerializedName("SundayRequestId") val sundayRequestId: String? = null,
    @SerializedName("EmpId") val empId: String? = null,
    @SerializedName("EMPName") val empName: String? = null,
    @SerializedName("Date") val date: String? = null,
    @SerializedName("RequestDate") val requestDate: String? = null,
    @SerializedName("Reason") val reason: String? = null,
    @SerializedName("ApprovedDisapproved") val approvedDisapproved: String? = null,
    @SerializedName("ApprovedDisapprovedbyUserId") val approvedByUserId: String? = null
)