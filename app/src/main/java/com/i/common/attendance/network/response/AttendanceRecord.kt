package com.i.common.attendance.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class AttendanceRecordResponse (
    @SerializedName("status"  ) var status  : String?           = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("result") var result: Any? = null
)
@Keep
data class AttendanceRecord(
    @SerializedName("AutoId") val autoId: String?,
    @SerializedName("MobileNo") val mobileNo: String?,
    @SerializedName("InTime") val inTime: String?,
    @SerializedName("OutTime") val outTime: String?,
    @SerializedName("Status") val status: String?,
    @SerializedName("Remarks") val remarks: String?,
    @SerializedName("Lat") val lat: String?,
    @SerializedName("Long") val long: String?,
    @SerializedName("GPSStatus") val gpsStatus: String?,
    @SerializedName("NetStatus") val netStatus: String?,
    @SerializedName("InsertedOn") val insertedOn: String?,
    @SerializedName("LastUpdatedOn") val lastUpdatedOn: String?,
    @SerializedName("InsertedByUserId") val insertedByUserId: String?,
    @SerializedName("LastUpdatedByUserId") val lastUpdatedByUserId: String?,
    @SerializedName("PhotoPath") val photoPath: String?,
    @SerializedName("FromKM") val fromKM: String?,
    @SerializedName("ToKM") val toKM: String?,
    @SerializedName("PhotoPath2") val photoPath2: String?,
    @SerializedName("IsMissPunch") val isMissPunch: String?,
    @SerializedName("GPSPhotoPath") val gpsPhotoPath: String?,
    @SerializedName("StatusMsgShow") val statusMsgShow: String?,
    @SerializedName("Today") val today: String?,
    @SerializedName("IsGetInOutTime") val isGetInOutTime: String?,
)
