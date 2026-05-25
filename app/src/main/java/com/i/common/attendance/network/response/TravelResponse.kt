package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TravelResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result")  val result:  Any?    = null
)

@Parcelize
data class TravelData(
    @SerializedName("Text") val text: String?,
    @SerializedName("TextListId") val textListId: String?
): Parcelable