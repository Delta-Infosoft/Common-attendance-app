package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class DailyTourDistrictResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result") var result: List<DailyTourDistrict>? = null
)
@Parcelize
data class DailyTourDistrict (
  @SerializedName("District" ) var District : String? = null,
  @SerializedName("DistrictId" ) var DistrictId : String? = null,
): Parcelable