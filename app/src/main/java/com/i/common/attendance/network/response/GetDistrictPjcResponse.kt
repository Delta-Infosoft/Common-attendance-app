package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetDistrictPjcResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<GetDistrictPjcList> = arrayListOf()
)

@Parcelize
data class GetDistrictPjcList (
  @SerializedName("DistrictId" ) var DistrictId : String? = null,
  @SerializedName("Name"       ) var Name       : String? = null
) : Parcelable