package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class PjcDateResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result") val result: List<PjcDateModel>? = emptyList()
)
data class PjcDateModel(
    @SerializedName("NoOfDays") val noOfDays: String? = null,
    @SerializedName("FromPJCDate") val fromPjcDate: String? = null,
    @SerializedName("ToPJCDate") val toPjcDate: String? = null
)