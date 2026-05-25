package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class CheckDealerInOutStatusResponse(

    val status: String? = "",

    val message: String? = "",

    val result: JsonElement? = null
)
data class CheckDealerInOutStatusData(

    @SerializedName("DealerCheckInId")
    val dealerCheckInId: String? = "",

    @SerializedName("MobileNo")
    val mobileNo: String? = "",

    @SerializedName("InTime")
    val inTime: String? = "",

    @SerializedName("OutTime")
    val outTime: String? = "",

    @SerializedName("Remarks")
    val remarks: String? = "",

    @SerializedName("Lat")
    val lat: String? = "",

    @SerializedName("Long")
    val long: String? = "",

    @SerializedName("InsertedOn")
    val insertedOn: String? = "",

    @SerializedName("LastUpdatedOn")
    val lastUpdatedOn: String? = "",

    @SerializedName("InsertedByUserId")
    val insertedByUserId: String? = "",

    @SerializedName("LastUpdatedByUserId")
    val lastUpdatedByUserId: String? = "",

    @SerializedName("GPSPhotoPath")
    val gpsPhotoPath: String? = "",

    @SerializedName("DealerCategoryId")
    val dealerCategoryId: String? = "",

    @SerializedName("DealerCategory")
    val dealerCategory: String? = "",

    @SerializedName("DealerId")
    val dealerId: String? = "",

    @SerializedName("DealerName")
    val dealerName: String? = "",

    @SerializedName("StatusMsgShow")
    val statusMsgShow: String? = "",

    @SerializedName("Today")
    val today: String? = ""
)
