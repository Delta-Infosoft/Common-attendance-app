package com.i.common.attendance.network.response

import androidx.annotation.Keep
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

@Keep
data class LoginResponse(
    val status: String,
    val message: String,
    val result: JsonElement?
)

@Keep
data class LoginUser(
    @SerializedName("AutoId"                 ) var AutoId                 : String? = null,
    @SerializedName("MobileNo"               ) var MobileNo               : String? = null,
    @SerializedName("IMEICode"               ) var IMEICode               : String? = null,
    @SerializedName("IsApproved"             ) var IsApproved             : String? = null,
    @SerializedName("ApprovedDateTime"       ) var ApprovedDateTime       : String? = null,
    @SerializedName("FCMId"                  ) var FCMId                  : String? = null,
    @SerializedName("LastLoginDateTime"      ) var LastLoginDateTime      : String? = null,
    @SerializedName("LastLogOutDateTime"     ) var LastLogOutDateTime     : String? = null,
    @SerializedName("Remarks"                ) var Remarks                : String? = null,
    @SerializedName("CompanyName"            ) var CompanyName            : String? = null,
    @SerializedName("InsertedOn"             ) var InsertedOn             : String? = null,
    @SerializedName("LastUpdatedOn"          ) var LastUpdatedOn          : String? = null,
    @SerializedName("InsertedByUserId"       ) var InsertedByUserId       : String? = null,
    @SerializedName("LastUpdatedByUserId"    ) var LastUpdatedByUserId    : String? = null,
    @SerializedName("UsersName"              ) var UsersName              : String? = null,
    @SerializedName("IsAutoSignOut"          ) var IsAutoSignOut          : String? = null,
    @SerializedName("IsAllowTourRights"      ) var IsAllowTourRights      : String? = null,
    @SerializedName("DepartmentId"           ) var DepartmentId           : String? = null,
    @SerializedName(value = "EmpId", alternate = ["EmpID"] ) var EmpID            : String? = null,
    @SerializedName("IsDisable"              ) var IsDisable              : String? = null,
    @SerializedName("Lat"                    ) var Lat                    : String? = null,
    @SerializedName("Long"                   ) var Long                   : String? = null,
    @SerializedName("IsAllowGeoFencing"      ) var IsAllowGeoFencing      : String? = null,
    @SerializedName("BackDatedRights"        ) var BackDatedRights        : String? = null,
    @SerializedName("isValidationWork"       ) var isValidationWork       : String? = null,
    @SerializedName("SalesPersonCode"        ) var SalesPersonCode        : String? = null,
    @SerializedName("IsCheckIn"              ) var IsCheckIn              : String? = null,
    @SerializedName("FromTime"               ) var FromTime               : String? = null,
    @SerializedName("ToTime"                 ) var ToTime                 : String? = null,
    @SerializedName("ExpenseRight"           ) var ExpenseRight           : String? = null,
    @SerializedName("IsAllowPJCWOValidation" ) var IsAllowPJCWOValidation : String? = null,
    @SerializedName("DailyCost" ) var DailyCost : String? = null,
)
