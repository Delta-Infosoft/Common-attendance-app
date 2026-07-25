package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetDivisionRespose (

  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result") var result: JsonElement? = null
)

@Parcelize
data class GetDivision (

  @SerializedName("DivisionId" ) var DivisionId : String? = null,
  @SerializedName("BranchName" ) var BranchName : String? = null

): Parcelable
