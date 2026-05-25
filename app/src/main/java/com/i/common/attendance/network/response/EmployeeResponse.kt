package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EmployeeResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result") val result: Any? = null
)

@Parcelize
data class EmployeeModel(
    @SerializedName("AutoId"              ) var AutoId              : String? = null,
    @SerializedName("TeamHeadMobileNo"    ) var TeamHeadMobileNo    : String? = null,
    @SerializedName("MobileNo"            ) var MobileNo            : String? = null,
    @SerializedName("InsertedOn"          ) var InsertedOn          : String? = null,
    @SerializedName("LastUpdatedOn"       ) var LastUpdatedOn       : String? = null,
    @SerializedName("InsertedByUserId"    ) var InsertedByUserId    : String? = null,
    @SerializedName("LastUpdatedByUserId" ) var LastUpdatedByUserId : String? = null,
    @SerializedName("UsersName"           ) var UsersName           : String? = null
): Parcelable