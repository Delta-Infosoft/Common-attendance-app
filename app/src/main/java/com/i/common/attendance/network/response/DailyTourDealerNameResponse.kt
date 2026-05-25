package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DailyTourDealerNameResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  //@SerializedName("result") var result: List<DailyTourDealerName>? = null
  @SerializedName("result")  val result:  Any?    = null
)

@Parcelize
data class DailyTourDealerName (
  @SerializedName("Name"           ) var Name           : String? = null,
  @SerializedName("Area"           ) var Area           : String? = null,
  @SerializedName("District"       ) var District       : String? = null,
  @SerializedName("BusinessCenter" ) var BusinessCenter : String? = null,
  @SerializedName("DealerId" ) var DealerId : String? = null,
): Parcelable