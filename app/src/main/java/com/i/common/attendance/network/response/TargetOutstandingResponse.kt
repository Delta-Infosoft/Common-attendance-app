package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class TargetOutstandingResponse(

    val status: String? = "",

    val message: String? = "",

    val result: JsonElement? = null
)

data class TargetOutstandingData(

    @SerializedName("TargetAmt")
    val targetAmt: String? = "",

    @SerializedName("AchievedAmt")
    val achievedAmt: String? = "",

    @SerializedName("OutstandingAmt")
    val outstandingAmt: String? = ""
)