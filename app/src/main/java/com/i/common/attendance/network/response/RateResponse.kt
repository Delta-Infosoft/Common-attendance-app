package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class RateResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null  // ✅ Fix: Any? instead of List<RateModel>
)
data class RateModel(
    @SerializedName("PLRate") val plRate: String? = null,
    @SerializedName("GST") val gst: String? = null
)