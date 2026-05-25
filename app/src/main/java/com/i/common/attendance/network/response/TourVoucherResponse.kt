package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class TourVoucherResponse(
    val status: String?,
    val message: String?,
    @SerializedName("result") var result: Any? = null
)
data class TourVoucherItem(
    val ExpenseId: String? = null,
    val EmpId: String? = null,
    val Designation: String? = null,
    val DeptId: String? = null,

    val TravelDt: String? = null,
    val TravelToDt: String? = null,

    val TravellingBy: String? = null,
    val FromPlace: String? = null,
    val ToPlace: String? = null,

    val StartTime: String? = null,
    val EndTime: String? = null,

    val NighHault: String? = null,

    val FareAmount: String? = null,
    val AutoCharges: String? = null,
    val Lodging: String? = null,
    val DailyAllowance: String? = null,
    val OtherExpenses: String? = null,
    val TotalExpenses: String? = null,

    val InsertedOn: String? = null,
    val LastUpdatedOn: String? = null,
    val InsertedByUserId: String? = null,
    val LastUpdatedByUserId: String? = null,

    var ApprovedDisapproved: String? = null,
    val ApprovedDisapprovedOn: String? = null,
    val ApprovedDisapprovedbyUserId: String? = null,

    val L1ApproveDisapprovedOn: String? = null,
    val L1ApprovedDisapprovedByUserId: String? = null,

    val AutoChargesDetail: String? = null,
    val OtherChargesDetail: String? = null,
    val FareAmountDetail: String? = null,

    val IsGSTFareAmt: String? = null,
    val IsGSTAutoCharges: String? = null,
    val IsGSTLodging: String? = null,
    val IsGSTOtherExpenses: String? = null,

    val PhotoPath: String? = null,
    var ApprovedDisapprovedRemarks: String? = null,

    // ✅ Newly Added
    val Name: String? = null,
    val TourReport: String? = null,
    val Status: String? = null
)