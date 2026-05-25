package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class DistrictTourAgendaTrackingResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result")  var result:  Any?    = null  // ✅ Any? to handle both List and ""
)
@Parcelize
data class DistrictTourAgendaTracking (
  @SerializedName("District" ) var District : String? = null
): Parcelable
