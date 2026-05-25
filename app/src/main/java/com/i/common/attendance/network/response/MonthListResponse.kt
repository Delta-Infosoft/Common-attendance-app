package com.i.common.attendance.network.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MonthListResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<MonthList> = arrayListOf()
)

@Parcelize
data class MonthList (
  @SerializedName("Month") var Month : String? = null
): Parcelable