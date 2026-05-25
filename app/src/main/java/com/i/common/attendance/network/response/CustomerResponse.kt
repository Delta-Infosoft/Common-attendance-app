package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null  // ✅ Fix: Any? instead of List<CustomerModel>
)

data class CustomerModel(
    @SerializedName("LgrId") val lgrId: String? = null,
    @SerializedName("Name") val name: String? = null
)