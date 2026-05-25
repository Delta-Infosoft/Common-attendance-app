package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PjcResponse(
    val status: String,
    val message: String,
    val result: JsonElement?
)

data class PJCItem(
    @SerializedName("PJCId") val pjcId: String?,
    @SerializedName("Dt") val dt: String?,
    @SerializedName("EmpId") val empId: String?,
    @SerializedName("Area") val area: String?,
    @SerializedName("MonthYr") val monthYr: String?,
    @SerializedName("TgtSalesAmt") val tgtSalesAmt: String?,
    @SerializedName("ProjectedNewCust") val projectedNewCust: String?,
    @SerializedName("ProjectedNewDealer") val projectedNewDealer: String?,
    @SerializedName("PJCLnId") val pjcLnId: String?,
    @SerializedName("IsDrop") val isDrop: String?,
    @SerializedName("DropReason") val dropReason: String?,
    @SerializedName("Notes") val notes: String?,
    @SerializedName("Place") val place: String?,
    @SerializedName("LnNo") val lnNo: String?,
    @SerializedName("PaymentCnt") val paymentCnt: String?,
    @SerializedName("OrderCnt") val orderCnt: String?
)

data class EventsCalModel(
    val pjcId: String,
    val date: String,
    val notes: String,
    val place: String,
    val pjcLnId: String,
    val isDrop: String,
    val dropReason: String
)

data class HolidayWeekOffModel(
    val calendarDate: Int,
    val calendarMonth: Int,
    val calendarYear: Int,
    val remarks: String,
    val type: String   // "WOFF" or "Holiday"
)
