package com.i.common.attendance.network.response

import com.google.gson.Gson

fun ForgotPasswordResponse.getPassword(): String? {
    return try {
        if (result == null || result.isJsonNull || result.asString == "") {
            null
        } else {
            val array = result.asJsonArray
            val list = Gson().fromJson(array, Array<ForgotPasswordResult>::class.java)
            list.firstOrNull()?.newPassword
        }
    } catch (e: Exception) {
        null
    }
}