package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("status")  val status:  String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null
)

data class OrderItem(
    @SerializedName("SOId")          val soId:          String? = null,
    @SerializedName("No")            val no:            String? = null,
    @SerializedName("Dt")            val dt:            String? = null,
    @SerializedName("VendorLgrName") val vendorLgrName: String? = null,
    @SerializedName("GrandTotalAmt") val grandTotalAmt: String? = null,
    @SerializedName("Print")         val print:         String? = null
)