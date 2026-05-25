package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class HolidayWeekOffResponse(
    val status: String,
    val message: String,
    val result: JsonElement?
)

data class HolidayWeekOffDto(
    @SerializedName("CalendarDate") val calendarDate: String,
    @SerializedName("Remarks") val remarks: String,
    @SerializedName("Type") val type: String
)