package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class InsertPjcEventResponse (

  @SerializedName("status"  ) var status  : String? = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("result"  ) var result  : InsertPjcEvent? = InsertPjcEvent()
)

data class InsertPjcEvent (
  @SerializedName("id" ) var id : String? = null
)