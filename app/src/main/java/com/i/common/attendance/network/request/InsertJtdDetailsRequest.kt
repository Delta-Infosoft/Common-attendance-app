package com.i.common.attendance.network.request

import okhttp3.MultipartBody
import java.io.Serializable

data class InsertJtdDetailsRequest(

    val empId: String? = null,
    val objective: String? = null,
    val targetDt: String? = null,
    val status: String? = "Close",
    val briefReport: String? = null,
    val remarks: String? = null,
    val closedDt: String? = null,
    val userId: String? = null,
    val busiCntrId: String? = null,
    val roadMapId: String? = null

) : Serializable {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        fun addIfNotNull(key: String, value: String?) {
            if (!value.isNullOrEmpty()) {
                builder.addFormDataPart(key, value)
            }
        }

        addIfNotNull("EmpId", empId)
        addIfNotNull("Objective", objective)
        addIfNotNull("TargetDt", targetDt)
        addIfNotNull("Status", status)
        addIfNotNull("BriefReport", briefReport)
        addIfNotNull("Remarks", remarks)
        addIfNotNull("ClosedDt", closedDt)
        addIfNotNull("UserId", userId)
        addIfNotNull("BusiCntrId", busiCntrId)
        addIfNotNull("RoadMapId", roadMapId)

        return builder.build()
    }
}