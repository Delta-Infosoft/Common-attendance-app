package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourAddDetailsDukeRequest(
    val date: String,
    val startTime: String,
    val endTime: String,
    val fromPlace: String,
    val toPlace: String,
    val typeTextListId: String,

    val dealerName: String,
    val dealerId: String? = null,

    val subDealerName: String,
    val subDealerId: String? = null,

    val district: String,
    val districtNew: String,

    val area: String,
    val businessCenter: String,

    val mobileNo: String,
    val pointDiscussion: String,

    val empMobileNo: String,

    val serviceCenterId: String? = null,

    val status: String? = null
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        // Required fields
        builder.addFormDataPart("Date", date)
        builder.addFormDataPart("StartTime", startTime)
        builder.addFormDataPart("EndTime", endTime)
        builder.addFormDataPart("FromPlace", fromPlace)
        builder.addFormDataPart("ToPlace", toPlace)
        builder.addFormDataPart("TypeTextListId", typeTextListId)

        builder.addFormDataPart("DealerName", dealerName)
        builder.addFormDataPart("SubDealerName", subDealerName)

        builder.addFormDataPart("District", district)
        builder.addFormDataPart("DistrictNew", districtNew)

        builder.addFormDataPart("Area", area)
        builder.addFormDataPart("BusinessCenter", businessCenter)

        builder.addFormDataPart("MobileNo", mobileNo)
        builder.addFormDataPart("PointDiscussion", pointDiscussion)

        builder.addFormDataPart("EmpMobileNo", empMobileNo)

        // Optional fields (null + empty handled here)
        if (!dealerId.isNullOrBlank()) {
            builder.addFormDataPart("DealerId", dealerId)
        }

        if (!subDealerId.isNullOrBlank()) {
            builder.addFormDataPart("SubDealerId", subDealerId)
        }

        if (!serviceCenterId.isNullOrBlank()) {
            builder.addFormDataPart("ServiceCenterId", serviceCenterId)
        }

        if (!status.isNullOrBlank()) {
            builder.addFormDataPart("Status", status)
        }

        return builder.build()
    }
}