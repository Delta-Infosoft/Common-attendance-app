package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResult(
    @SerializedName("NewPassword")
    val newPassword: String?
)