package com.i.common.attendance.network.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class ReasonResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<ReasonList> = arrayListOf()
)

@Parcelize
data class ReasonList (
  @SerializedName("ReasonWiseDetailId"     ) var ReasonWiseDetailId     : String? = null,
  @SerializedName("ReasonTypeTextlistId"   ) var ReasonTypeTextlistId   : String? = null,
  @SerializedName("ReasonName"             ) var ReasonName             : String? = null,
  @SerializedName("IsDisabled"             ) var IsDisabled             : String? = null,
  @SerializedName("InsertedOn"             ) var InsertedOn             : String? = null,
  @SerializedName("LastUpdatedOn"          ) var LastUpdatedOn          : String? = null,
  @SerializedName("InsertedByUserId"       ) var InsertedByUserId       : String? = null,
  @SerializedName("LastUpdatedByUserId"    ) var LastUpdatedByUserId    : String? = null,
  @SerializedName("IsMultipleEntryAllowed" ) var IsMultipleEntryAllowed : String? = null,
  @SerializedName("ReportShowSeqNo"        ) var ReportShowSeqNo        : String? = null,
  @SerializedName("ReasonType"             ) var ReasonType             : String? = null
) : Parcelable