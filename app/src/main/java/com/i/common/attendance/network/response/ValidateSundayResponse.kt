package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class ValidateSundayResponse(
    val status: String,
    val message: String,
    @SerializedName("result")  val result:  Any?    = null
)

data class ValidateSundayResult(
    val Status: String?
)