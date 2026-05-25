package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TravelingByResponse(
    val status: String?,
    val message: String?,
    val result: JsonElement?
)
@Parcelize
data class TravelingByItem(
    @SerializedName("TextListId"          ) var TextListId          : String? = null,
    @SerializedName("Group"               ) var Group               : String? = null,
    @SerializedName("Text"                ) var Text                : String? = null,
    @SerializedName("InsertedOn"          ) var InsertedOn          : String? = null,
    @SerializedName("LastUpdatedOn"       ) var LastUpdatedOn       : String? = null,
    @SerializedName("InsertedByUserId"    ) var InsertedByUserId    : String? = null,
    @SerializedName("LastUpdatedByUserId" ) var LastUpdatedByUserId : String? = null,
    @SerializedName("IsGetInOutTime"      ) var IsGetInOutTime      : String? = null
): Parcelable
