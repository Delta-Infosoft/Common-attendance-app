package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class LoadDropDownListResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<LoadDropDownList> = arrayListOf()
)

@Parcelize
data class LoadDropDownList (
  @SerializedName("Id"   ) var Id   : String? = null,
  @SerializedName("Name" ) var Name : String? = null
): Parcelable
