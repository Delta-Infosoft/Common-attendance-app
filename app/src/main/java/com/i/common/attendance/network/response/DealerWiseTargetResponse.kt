package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DealerWiseTargetResponse (

  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : JsonElement?= null
)

@Parcelize
data class DealerWiseTarget (
  @SerializedName("TextListId" ) var TextListId : String? = null,
  @SerializedName("Text"       ) var Text       : String? = null,
  @SerializedName("AvgRate"    ) var AvgRate    : String? = null,
  var qty: Double = 0.0
): Parcelable
