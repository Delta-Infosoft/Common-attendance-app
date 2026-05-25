package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TourAgendaTrackingServiceCenterResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result")  var result:  Any?    = null  // ✅ Fix: Any? instead of ArrayList<Result>
)

@Parcelize
data class TourAgendaTrackingServiceCenter (
  @SerializedName("LgrId" ) var SubDealerId : String? = null,
  @SerializedName("Name"     ) var Name     : String? = null,
): Parcelable