package com.i.common.attendance.network.response
import com.google.gson.annotations.SerializedName


data class ServerTimeResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<ServerTime> = arrayListOf()
)

data class ServerTime (
  @SerializedName("serverTime" ) var serverTime : String? = null
)