package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class GetRecords (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result") var result: Any? = null
)

data class Records(
  @SerializedName("InTime"  ) var InTime  : String? = null,
  @SerializedName("OutTime" ) var OutTime : String? = null,
  @SerializedName("Status"  ) var Status  : String? = null,
  @SerializedName("Remarks" ) var Remarks : String? = null,
  @SerializedName("AutoId"  ) var AutoId  : String? = null
)