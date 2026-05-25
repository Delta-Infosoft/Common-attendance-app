package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class PjcPermissionResponse(
    val status: String?,
    val message: String?,
    val result: List<PjcPermissionResult>?
)
data class PjcPermissionResult(
    @SerializedName("Column1") val column1: String? = null,
    @SerializedName("AllowTourWithoutPJC") val allowTourWithoutPjc: String? = null,
    @SerializedName("AllowPJC") val allowPjc: String? = null
)