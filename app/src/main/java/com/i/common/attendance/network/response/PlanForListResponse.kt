package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PlanForListResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<PlanForList> = arrayListOf()
)

@Parcelize
data class  PlanForList (
  @SerializedName("PlanForTextListId" ) var PlanForTextListId : String? = null,
  @SerializedName("PlanFor"           ) var PlanFor           : String? = null
) : Parcelable