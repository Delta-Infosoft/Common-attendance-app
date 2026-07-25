package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName


data class LeaveCounterResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result") var result: JsonElement? = null
)

data class LeaveCounter (
  @SerializedName("LeaveCount" ) var LeaveCount : String? = null
)