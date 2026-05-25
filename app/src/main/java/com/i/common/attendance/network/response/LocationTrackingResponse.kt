package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class LocationTrackingResponse (
  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("result"  ) var result  : Result? = Result()
)

data class Result (
  @SerializedName("id" ) var id : String? = null
)