package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingAddMeetingRequest(
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val fromPlace: String? = null,
    val toPlace: String? = null,
    val typeTextListId: String? = null,
    val dealerName: String? = null,
    val subDealerName: String? = null,
    val district: String? = null,
    val area: String? = null,
    val businessCenter: String? = null,
    val mobileNo: String? = null,
    val pointDiscussion: String? = null,
    val empMobileNo: String? = null,
    val districtNew: String? = null,
    val status: String? = null,
    val subDealerId: String? = null,
    val serviceCenterId: String? = null
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        fun addIfNotNull(key: String, value: String?) {
            if (!value.isNullOrEmpty()) {
                builder.addFormDataPart(key, value)
            }
        }

        addIfNotNull("Date", date)
        addIfNotNull("StartTime", startTime)
        addIfNotNull("EndTime", endTime)
        addIfNotNull("FromPlace", fromPlace)
        addIfNotNull("ToPlace", toPlace)
        addIfNotNull("TypeTextListId", typeTextListId)
        addIfNotNull("DealerName", dealerName)
        addIfNotNull("SubDealerName", subDealerName)
        addIfNotNull("District", district)
        addIfNotNull("Area", area)
        addIfNotNull("BusinessCenter", businessCenter)
        addIfNotNull("MobileNo", mobileNo)
        addIfNotNull("PointDiscussion", pointDiscussion)
        addIfNotNull("EmpMobileNo", empMobileNo)
        addIfNotNull("DistrictNew", districtNew)
        addIfNotNull("Status", status)
        addIfNotNull("SubDealerId", subDealerId)
        addIfNotNull("ServiceCenterId", serviceCenterId)

        return builder.build()
    }
}