package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ApiResponse(
    val status: String,
    val message: String,
    val result: List<DropdownItem>
)

@Parcelize
data class DropdownItem(
    @SerializedName("Text")
    val text: String
): Parcelable




