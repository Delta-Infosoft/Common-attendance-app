package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class TourAgendaTrackingStartEndMeetingRequest(
    val dealerId: String? = null,
    val dealerName: String? = null,
    val empId: String? = null,
    val inTime: String? = null,
    val mobileNo: String? = null,
    val outTime: String? = null,
    val autoId: String? = null,
    val status: String? = null,
    val momRemarks: String? = null,
    val userId: String? = null,
    val roadMapId: String? = null,
    val stateId: String? = null,
    val districtId: String? = null,
    val businessCenterId: String? = null,
    val subDealerId: String? = null,
    val serviceCenterId: String? = null
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        fun addIfNotNull(key: String, value: String?) {
            if (value != null){
                builder.addFormDataPart(key, value)
            }
        }

        addIfNotNull("DealerId", dealerId)
        addIfNotNull("DealerName", dealerName)
        addIfNotNull("EmpId", empId)
        addIfNotNull("InTime", inTime)
        addIfNotNull("MobileNo", mobileNo)
        addIfNotNull("OutTime", outTime)
        addIfNotNull("AutoId", autoId)
        addIfNotNull("Status", status)
        addIfNotNull("MOMRemarks", momRemarks)
        addIfNotNull("UserId", userId)
        addIfNotNull("RoadMapId", roadMapId)
        addIfNotNull("StateId", stateId)
        addIfNotNull("DistrictId", districtId)
        addIfNotNull("BusinessCenterId", businessCenterId)
        addIfNotNull("SubDealerId", subDealerId)
        addIfNotNull("ServiceCenterId", serviceCenterId)

        return builder.build()
    }
}