package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName


data class AttendanceInOutResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<AttendanceInOutReport> = arrayListOf()
)

data class AttendanceInOutReport(

  @SerializedName("AutoId"              ) var AutoId              : String? = null,
  @SerializedName("MobileNo"            ) var MobileNo            : String? = null,
  @SerializedName("InTime"              ) var InTime              : String? = null,
  @SerializedName("OutTime"             ) var OutTime             : String? = null,
  @SerializedName("Status"              ) var Status              : String? = null,
  @SerializedName("Remarks"             ) var Remarks             : String? = null,
  @SerializedName("Lat"                 ) var Lat                 : String? = null,
  @SerializedName("Long"                ) var Long                : String? = null,
  @SerializedName("GPSStatus"           ) var GPSStatus           : String? = null,
  @SerializedName("NetStatus"           ) var NetStatus           : String? = null,
  @SerializedName("InsertedOn"          ) var InsertedOn          : String? = null,
  @SerializedName("LastUpdatedOn"       ) var LastUpdatedOn       : String? = null,
  @SerializedName("InsertedByUserId"    ) var InsertedByUserId    : String? = null,
  @SerializedName("LastUpdatedByUserId" ) var LastUpdatedByUserId : String? = null,
  @SerializedName("PhotoPath"           ) var PhotoPath           : String? = null,
  @SerializedName("FromKM"              ) var FromKM              : String? = null,
  @SerializedName("ToKM"                ) var ToKM                : String? = null,
  @SerializedName("PhotoPath2"          ) var PhotoPath2          : String? = null,
  @SerializedName("IsMissPunch"         ) var IsMissPunch         : String? = null,
  @SerializedName("GPSPhotoPath"        ) var GPSPhotoPath        : String? = null,
  @SerializedName("StatusMsgShow"       ) var StatusMsgShow       : String? = null,
  @SerializedName("Today"               ) var Today               : String? = null,
  @SerializedName("IsGetInOutTime"      ) var IsGetInOutTime      : String? = null

)