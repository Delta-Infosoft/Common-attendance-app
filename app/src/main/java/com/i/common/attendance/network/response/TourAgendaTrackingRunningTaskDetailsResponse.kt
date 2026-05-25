package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class TourAgendaTrackingRunningTaskDetailsResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null  // ✅ Fix: Any? instead of ArrayList<RunningTaskItem>
)

data class TourAgendaTrackingRunningTaskDetails(
    @SerializedName("AutoId") val autoId: String? = null,
    @SerializedName("DealerId") val dealerId: String? = null,
    @SerializedName("EmpId") val empId: String? = null,
    @SerializedName("InTime") val inTime: String? = null,
    @SerializedName("OutTime") val outTime: String? = null,
    @SerializedName("MobileNo") val mobileNo: String? = null,
    @SerializedName("DealerObjectiveId") val dealerObjectiveId: String? = null,
    @SerializedName("Objective") val objective: String? = null,
    @SerializedName("BriefofObjective") val briefOfObjective: String? = null,
    @SerializedName("Status") val status: String? = null,
    @SerializedName("SubDealerId") val subDealerId: String? = null,
    @SerializedName("ServiceCenterId") val serviceCenterId: String? = null,
    @SerializedName("DealerCategoryTextListId") val dealerCategoryTextListId: String? = null,
    @SerializedName("SubDealerName") val subDealerName: String? = null,
    @SerializedName("ServiceCenterName") val serviceCenterName: String? = null
)