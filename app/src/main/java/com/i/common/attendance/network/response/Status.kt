package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName


data class Status (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<StatusList> = arrayListOf()
)

data class StatusList(
  @SerializedName("TextListId"          ) var TextListId          : String? = null,
  @SerializedName("Group"               ) var Group               : String? = null,
  @SerializedName("Text"                ) var Text                : String? = null,
  @SerializedName("InsertedOn"          ) var InsertedOn          : String? = null,
  @SerializedName("LastUpdatedOn"       ) var LastUpdatedOn       : String? = null,
  @SerializedName("InsertedByUserId"    ) var InsertedByUserId    : String? = null,
  @SerializedName("LastUpdatedByUserId" ) var LastUpdatedByUserId : String? = null,
  @SerializedName("IsGetInOutTime"      ) var IsGetInOutTime      : String? = null
)