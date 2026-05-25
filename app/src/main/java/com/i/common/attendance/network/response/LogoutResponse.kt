package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String?
)