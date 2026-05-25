package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TourAgendaTrackingSubDealerNameResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result") var result: Any? = null  // ✅ Fix: Any? instead of ArrayList<Result>
)

@Parcelize
data class TourAgendaTrackingSubDealerName(
    @SerializedName("SubDealerId") var SubDealerId: String? = null,
    @SerializedName("Name") var Name: String? = null,
    @SerializedName("District") var District: String? = null,
    @SerializedName("BusinessCenter") var BusinessCenter: String? = null,
    @SerializedName("Area") var Area: String? = null,
) : Parcelable