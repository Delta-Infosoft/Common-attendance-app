package com.i.common.attendance.network.response

import com.google.gson.JsonElement

data class ForgotPasswordResponse(
    val status: String,
    val message: String?,
    val result: JsonElement?
)