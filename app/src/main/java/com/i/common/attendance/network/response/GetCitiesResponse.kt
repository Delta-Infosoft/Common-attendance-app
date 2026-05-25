package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetCitiesResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result")  val result:  Any?    = null
)
@Parcelize
data class GetCities(
    @SerializedName("Name") val name: String?,
    @SerializedName("TADACityId") val tadaCityId: String?,
    @SerializedName("CityId") val cityId: String?,
    @SerializedName("City") val city: String?
): Parcelable