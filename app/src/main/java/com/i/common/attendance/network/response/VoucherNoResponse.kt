package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class VoucherNoResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result")  val result:  Any?    = null
)

data class VoucherNoData(
    @SerializedName("No") val no: String?
)