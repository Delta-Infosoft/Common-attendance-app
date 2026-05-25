package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NameDropdownResponse(
    val status: String,
    val message: String,
    val result: List<NameDropdownItem>
)

@Parcelize
data class NameDropdownItem(
    @SerializedName("Name")
    val name: String
): Parcelable