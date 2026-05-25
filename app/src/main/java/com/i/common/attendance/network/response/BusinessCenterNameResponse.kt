package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class BusinessCenterNameResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result")  var result:  Any?    = null  // ✅ Any? to handle both List and ""
)

@Parcelize
data class BusinessCenterName (
  @SerializedName("BusiCntrId" ) var BusiCntrId : String? = null,
  @SerializedName("Name"       ) var Name       : String? = null
): Parcelable