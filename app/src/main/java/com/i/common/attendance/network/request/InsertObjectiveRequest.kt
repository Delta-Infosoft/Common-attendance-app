package com.i.common.attendance.network.request

import okhttp3.MultipartBody
import java.io.Serializable

data class InsertObjectiveRequest(
    val dealerId: String? = null,
    val mobileNo: String? = null,
    val autoId: String? = null,
    val empId: String? = null,
    val objective: String? = null,
    val briefOfObjective: String? = null,
    val status: String? = null,
    val dealerObjectiveId: String? = null,
    val roadMapId: String? = null
) : Serializable {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        fun addIfNotNull(key: String, value: String?) {
            if (value != null) {
                builder.addFormDataPart(key, value)
            }
        }

        addIfNotNull("DealerId", dealerId)
        addIfNotNull("MobileNo", mobileNo)
        addIfNotNull("AutoId", autoId)
        addIfNotNull("EmpId", empId)
        addIfNotNull("Objective", objective)
        addIfNotNull("BriefofObjective", briefOfObjective)
        addIfNotNull("Status", status)
        addIfNotNull("DealerObjectiveId", dealerObjectiveId)
        addIfNotNull("RoadMapId", roadMapId)

        return builder.build()
    }
}