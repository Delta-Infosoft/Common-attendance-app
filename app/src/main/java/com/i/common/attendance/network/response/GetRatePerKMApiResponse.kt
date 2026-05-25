package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class GetRatePerKMApiResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result")  val result:  Any?    = null
)

data class GetRatePerKM(
    @SerializedName("RatePerKMS") val ratePerKMS: String?,
    @SerializedName("TravelingTypeMasterLnId") val travelingTypeMasterLnId: String?,
    @SerializedName("RateType") val rateType: String?
)