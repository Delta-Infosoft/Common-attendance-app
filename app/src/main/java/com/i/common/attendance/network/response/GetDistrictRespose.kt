package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class GetDistrictRespose (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result") var result: JsonElement? = null
)


@Parcelize
data class GetDistrict (
  @SerializedName("DistrictId" ) var DistrictId : String? = null,
  @SerializedName("Name"       ) var Name       : String? = null
): Parcelable