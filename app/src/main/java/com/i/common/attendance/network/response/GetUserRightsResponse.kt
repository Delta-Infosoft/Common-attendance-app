package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class GetUserRightsResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result")  var result:  Any?    = null
)

data class UserRightsItem(
    @SerializedName("IsCheckIn") var isCheckIn: String? = null,
    @SerializedName("RequestDate") var requestDate: String? = null
)