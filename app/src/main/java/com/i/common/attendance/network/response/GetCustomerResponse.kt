package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import kotlinx.parcelize.Parcelize

data class GetCustomerResponse(
    val status: String? = null,
    val message: String? = null,
    //val result: List<CustomerData>? = emptyList()
    val result: JsonElement? = null
)

@Parcelize
data class CustomerData(
    val LgrId: String? = null,
    val Name: String? = null
): Parcelable