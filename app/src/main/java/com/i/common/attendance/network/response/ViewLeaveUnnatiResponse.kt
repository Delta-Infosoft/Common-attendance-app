package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ViewLeaveUnnatiResponse (

  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result: JsonElement? = null

)

data class ViewLeaveUnnatiList (

  @SerializedName("LeaveRequestId"        ) var LeaveRequestId        : String? = null,
  @SerializedName("EmpId"                 ) var EmpId                 : String? = null,
  @SerializedName("FromDt"                ) var FromDt                : String? = null,
  @SerializedName("ToDt"                  ) var ToDt                  : String? = null,
  @SerializedName("LeaveReson"            ) var LeaveReson            : String? = null,
  @SerializedName("InsertedOn"            ) var InsertedOn            : String? = null,
  @SerializedName("LastUpdatedOn"         ) var LastUpdatedOn         : String? = null,
  @SerializedName("InsertedByUserId"      ) var InsertedByUserId      : String? = null,
  @SerializedName("LastUpdatedByUserId"   ) var LastUpdatedByUserId   : String? = null,
  @SerializedName("ApprovedDisapproved"   ) var ApprovedDisapproved   : String? = null,
  @SerializedName("ApprovedDisapprovedOn" ) var ApprovedDisapprovedOn : String? = null,
  @SerializedName("ApprovedDisapprovedBy" ) var ApprovedDisapprovedBy : String? = null,
  @SerializedName("LeaveFor"              ) var LeaveFor              : String? = null,
  @SerializedName("Employee"              ) var Employee              : String? = null,
  @SerializedName("SApprovedDisapproved"  ) var SApprovedDisapproved  : String? = null

)