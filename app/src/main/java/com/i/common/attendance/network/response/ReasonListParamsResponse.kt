package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName


data class ReasonListParamsResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<ReasonListParamsList> = arrayListOf()
)

data class ReasonListParamsList (
  @SerializedName("ReasonWiseDetailLnId"      ) var ReasonWiseDetailLnId      : String? = null,
  @SerializedName("ReasonWiseDetailId"        ) var ReasonWiseDetailId        : String? = null,
  @SerializedName("ReasonParameter"           ) var ReasonParameter           : String? = null,
  @SerializedName("LnNo"                      ) var LnNo                      : String? = null,
  @SerializedName("EntryMode"                 ) var EntryMode                 : String? = null,
  @SerializedName("SQLQuery"                  ) var SQLQuery                  : String? = null,
  @SerializedName("IsDisabled"                ) var IsDisabled                : String? = null,
  @SerializedName("SeqNo"                     ) var SeqNo                     : String? = null,
  @SerializedName("TransactionTypeTextListId" ) var TransactionTypeTextListId : String? = null,
  @SerializedName("LblParameter"              ) var LblParameter              : String? = null,
  @SerializedName("ValueParameter"            ) var ValueParameter            : String? = null,
  @SerializedName("DisabledControl"           ) var DisabledControl           : String? = null,
  @SerializedName("EntryType"                 ) var EntryType                 : String? = null,
  @SerializedName("IsCompulsory"              ) var IsCompulsory              : String? = null,
  @SerializedName("MinLength"                 ) var MinLength                 : String? = null,
  @SerializedName("MaxLength"                 ) var MaxLength                 : String? = null,
  @SerializedName("SQLQuery2"                 ) var SQLQuery2                 : String? = null
)

data class PjcEventInsertReasonDynamicParams(
  @SerializedName("LnNo"                      ) var LnNo                      : String? = null,
  @SerializedName("ReasonWiseDetailLnId"      ) var ReasonWiseDetailLnId      : String? = null,
  @SerializedName("LblParameter"              ) var LblParameter              : String? = null,
  @SerializedName("ValueParameter"            ) var ValueParameter            : String? = null,
)