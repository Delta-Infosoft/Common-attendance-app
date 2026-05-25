package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class CheckPJCEntryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("result") val result: Any?
)

data class PJCEntryItem(
    @SerializedName("CalendarDate") val calendarDate: String?,
    @SerializedName("EmpId") val empId: String?,
    @SerializedName("Type") val type: String?
)