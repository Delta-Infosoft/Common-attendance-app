package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GetStateResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result")  var result:  Any?    = null
)

@Parcelize
data class GetState (
  @SerializedName("StateTextListId" ) var StateTextListId : String? = null,
  @SerializedName("State"           ) var State           : String? = null
): Parcelable